import urllib.request
import json
import time

BASE_URL = "https://api.sonzaix.indevs.in"
PROVIDER = "melolo"

def test_url(url, description):
    print(f"Testing {description}...")
    print(f"URL: {url}")
    start = time.time()
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req, timeout=10) as response:
            status = response.status
            data = response.read().decode('utf-8')
            latency = int((time.time() - start) * 1000)
            
            # Try to parse JSON
            parsed = json.loads(data)
            print(f"-> STATUS: {status} | LATENCY: {latency}ms | TYPE: {type(parsed).__name__}")
            
            # Print a clean snippet
            snippet = json.dumps(parsed, indent=2)[:300] + "..."
            print("Snippet of response:")
            print(snippet)
            print("-" * 50)
            return parsed
    except Exception as e:
        latency = int((time.time() - start) * 1000)
        print(f"-> ERROR: {e} | LATENCY: {latency}ms")
        print("-" * 50)
        return None

def main():
    print("==============================================")
    print("         SONZAIX API ENDPOINT TESTER          ")
    print("==============================================")
    
    # 1. Test Root Endpoint
    test_url(f"{BASE_URL}/", "Root API Status Check")
    
    # 2. Test Languages
    test_url(f"{BASE_URL}/{PROVIDER}/languages", "Provider Languages")
    
    # 3. Test Home (and grab a drama ID)
    home_data = test_url(f"{BASE_URL}/{PROVIDER}/home?lang=id", "Home Feed")
    
    drama_id = None
    if home_data:
        # Try to find a drama ID
        try:
            if isinstance(home_data, list) and len(home_data) > 0:
                drama_id = home_data[0].get("id") or home_data[0].get("dramaId")
            elif isinstance(home_data, dict):
                # Search inside keys like 'dramas', 'list', etc.
                for key in ["dramas", "list", "data"]:
                    if key in home_data and isinstance(home_data[key], list) and len(home_data[key]) > 0:
                        drama_id = home_data[key][0].get("id") or home_data[key][0].get("dramaId")
                        break
        except Exception:
            pass

    # 4. Test New/Latest
    test_url(f"{BASE_URL}/{PROVIDER}/new?lang=id&page=1", "Latest/New List")
    
    # 5. Test Popular
    test_url(f"{BASE_URL}/{PROVIDER}/populer?lang=id&page=1", "Popular List")
    
    # 6. Test Search
    search_data = test_url(f"{BASE_URL}/{PROVIDER}/search?query=cinta&lang=id&page=1", "Search Query")
    if not drama_id and search_data:
        try:
            if isinstance(search_data, list) and len(search_data) > 0:
                drama_id = search_data[0].get("id")
            elif isinstance(search_data, dict):
                for key in ["dramas", "list", "data"]:
                    if key in search_data and isinstance(search_data[key], list) and len(search_data[key]) > 0:
                        drama_id = search_data[key][0].get("id")
                        break
        except Exception:
            pass

    # 7. Test Detail
    episode_id = None
    if drama_id:
        print(f"Found drama ID for detail test: {drama_id}")
        # Detail endpoints sometimes require different param keys: id, drama_id, book_id
        for param in ["id", "drama_id", "book_id"]:
            detail_data = test_url(f"{BASE_URL}/{PROVIDER}/detail?{param}={drama_id}", f"Detail Feed (param: {param})")
            if detail_data:
                # Try to extract an episode ID
                try:
                    episodes = None
                    if isinstance(detail_data, dict):
                        episodes = detail_data.get("episodes") or detail_data.get("episode_list")
                    elif isinstance(detail_data, list) and len(detail_data) > 0:
                        episodes = detail_data[0].get("episodes")
                        
                    if episodes and isinstance(episodes, list) and len(episodes) > 0:
                        episode_id = episodes[0].get("id") or episodes[0].get("episodeId")
                        break
                except Exception:
                    pass
    else:
        print("Skipping Detail Test: No drama ID found from Home/Search")

    # 8. Test Stream
    if drama_id and episode_id:
        print(f"Found episode ID for stream test: {episode_id}")
        for param in ["id", "episode_id"]:
            test_url(f"{BASE_URL}/{PROVIDER}/stream?{param}={episode_id}", f"Stream Source Feed (param: {param})")
    elif drama_id:
        # Fallback stream test with episode number 1
        print("No episode ID found, trying fallback stream test with drama_id & episode number...")
        test_url(f"{BASE_URL}/{PROVIDER}/stream?drama_id={drama_id}&episode=1", "Stream Source Feed (fallback)")
    else:
        print("Skipping Stream Test: No drama/episode ID found")

if __name__ == "__main__":
    main()
