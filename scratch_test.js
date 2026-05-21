const BASE_URL = "https://api.sonzaix.indevs.in";

const PROVIDERS = [
    {
        id: 'melolo',
        name: 'Melolo Short Drama',
        searchParam: 'q',
        hasLanguages: true,
        detailParams: (dramaId) => ({ id: dramaId, lang: 'id' }),
        streamParams: (dramaId, epId, epNum) => ({ id: dramaId, ep: epNum || 1 })
    },
    {
        id: 'freereels',
        name: 'FreeReels Short Drama',
        searchParam: 'q',
        hasLanguages: true,
        detailParams: (dramaId) => ({ id: dramaId, lang: 'id' }),
        streamParams: (dramaId, epId, epNum) => ({ dramaId: dramaId, episode: epNum || 1, lang: 'id' })
    },
    {
        id: 'flickreels',
        name: 'FlickReels Short Drama',
        searchParam: 'q',
        hasLanguages: false,
        detailParams: (dramaId) => ({ id: dramaId }),
        streamParams: (dramaId, epId, epNum) => ({ id: dramaId, ep: epNum || 1 })
    },
    {
        id: 'dramawave',
        name: 'DramaWave Short Drama',
        searchParam: 'q',
        hasLanguages: true,
        detailParams: (dramaId) => ({ id: dramaId, lang: 'id' }),
        streamParams: (dramaId, epId, epNum) => ({ dramaId: dramaId, episode: epNum || 1, lang: 'id' })
    },
    {
        id: 'dramanova',
        name: 'DramaNova Short Drama',
        searchParam: 'q',
        hasLanguages: false,
        detailParams: (dramaId) => ({ id: dramaId }),
        streamParams: (dramaId, epId, epNum) => ({ id: dramaId, ep: epNum || 1 })
    },
    {
        id: 'meloshort',
        name: 'MeloShort Drama',
        searchParam: 'query',
        hasLanguages: false,
        detailParams: (dramaId) => ({ drama_id: dramaId }),
        streamParams: (dramaId, epId, epNum) => ({ drama_id: dramaId, chapter_id: epId || '' })
    },
    {
        id: 'reelshort',
        name: 'ReelShort Drama',
        searchParam: 'q',
        hasLanguages: false,
        detailParams: (dramaId) => ({ id: dramaId }),
        streamParams: (dramaId, epId, epNum) => ({ id: dramaId, episode_no: epNum || 1 })
    },
    {
        id: 'netshort',
        name: 'NetShort Drama',
        searchParam: 'query',
        hasLanguages: false,
        detailParams: (dramaId) => ({ id: dramaId }),
        streamParams: (dramaId, epId, epNum) => ({ id: dramaId, episode_no: epNum || 1 })
    },
    {
        id: 'shortmax',
        name: 'ShortMax Drama',
        searchParam: 'q',
        hasLanguages: false,
        detailParams: (dramaId) => ({ id: dramaId, episode_limit: 100 }),
        streamParams: (dramaId, epId, epNum) => ({ id: dramaId, episode_no: epNum || 1 })
    },
    {
        id: 'dramabox',
        name: 'DramaBox Short Drama',
        searchParam: 'q',
        hasLanguages: false,
        detailParams: (dramaId) => ({ bookId: dramaId, lang: 'in' }),
        streamParams: (dramaId, epId, epNum) => ({ bookId: dramaId, chapterIndex: (epNum !== null ? epNum - 1 : 0), lang: 'in' })
    },
    {
        id: 'goodshort',
        name: 'GoodShort Short Drama',
        searchParam: 'q',
        hasLanguages: false,
        detailParams: (dramaId) => ({ bookId: dramaId }),
        streamParams: (dramaId, epId, epNum) => ({ bookId: dramaId })
    }
];

function findDramaDetails(obj) {
    const results = [];
    function traverse(current) {
        if (!current || typeof current !== 'object') return;
        if (Array.isArray(current)) {
            for (const item of current) {
                traverse(item);
            }
            return;
        }
        const keys = Object.keys(current);
        const hasId = keys.some(k => ['drama_id', 'dramaId', 'bookId', 'book_id', 'shortPlayId', 'id', 'playletId'].includes(k));
        const hasName = keys.some(k => ['drama_name', 'dramaName', 'book_name', 'bookName', 'title', 'name', 'playletName', 'drama_title'].includes(k));
        if (hasId && (hasName || keys.length < 15)) {
            let id = null;
            for (const k of ['drama_id', 'dramaId', 'bookId', 'book_id', 'shortPlayId', 'id', 'playletId']) {
                if (current[k]) { id = String(current[k]); break; }
            }
            let name = null;
            for (const k of ['drama_name', 'dramaName', 'book_name', 'bookName', 'title', 'name', 'playletName', 'drama_title']) {
                if (current[k]) { name = String(current[k]); break; }
            }
            if (id) {
                results.push({ id, name });
            }
        }
        for (const val of Object.values(current)) {
            if (val && typeof val === 'object') {
                traverse(val);
            }
        }
    }
    traverse(obj);
    return results;
}

function findEpisodeDetails(obj) {
    const results = [];
    function traverse(current) {
        if (!current || typeof current !== 'object') return;
        if (Array.isArray(current)) {
            for (const item of current) {
                traverse(item);
            }
            return;
        }
        const keys = Object.keys(current);
        const hasEpId = keys.some(k => ['episodeId', 'episode_id', 'chapter_id', 'chapterId', 'id', 'fileId'].includes(k));
        const hasEpNum = keys.some(k => ['episode', 'episode_no', 'episodeNo', 'chapter_no', 'chapterNo', 'index', 'chapterIndex', 'episodeNo'].includes(k));
        if (hasEpId || hasEpNum) {
            let id = null;
            for (const k of ['episodeId', 'episode_id', 'chapter_id', 'chapterId', 'id', 'fileId']) {
                if (current[k]) { id = String(current[k]); break; }
            }
            let num = null;
            for (const k of ['episode', 'episode_no', 'episodeNo', 'chapter_no', 'chapterNo', 'index', 'chapterIndex', 'episodeNo']) {
                if (current[k] !== undefined && current[k] !== null) { num = Number(current[k]); break; }
            }
            if (id || num !== null) {
                results.push({ id, num });
            }
        }
        for (const val of Object.values(current)) {
            if (val && typeof val === 'object') {
                traverse(val);
            }
        }
    }
    traverse(obj);
    return results.sort((a, b) => (a.num || 0) - (b.num || 0));
}

async function testUrl(url, description) {
    const start = Date.now();
    try {
        const res = await fetch(url, {
            headers: { 'User-Agent': 'Mozilla/5.0' },
            signal: AbortSignal.timeout(8000) // 8s timeout
        });
        const latency = Date.now() - start;
        const text = await res.text();
        let parsed = null;
        let isJson = false;
        try {
            parsed = JSON.parse(text);
            isJson = true;
        } catch (e) {}

        return {
            ok: res.ok,
            status: res.status,
            latency,
            isJson,
            data: parsed,
            rawSnippet: text.substring(0, 150)
        };
    } catch (err) {
        return {
            ok: false,
            status: 'ERR',
            latency: Date.now() - start,
            isJson: false,
            error: err.message
        };
    }
}

async function main() {
    console.log("=================================================");
    console.log("   SONZAIX ALL-PROVIDERS ENDPOINT TEST SUITE     ");
    console.log("=================================================");

    const report = [];

    // 1. Root Status Check
    console.log("\nTesting Root API...");
    const rootRes = await testUrl(`${BASE_URL}/`, "Root API");
    console.log(`Root status: ${rootRes.status} | Latency: ${rootRes.latency}ms`);
    report.push({ provider: 'Root API', endpoint: '/', status: rootRes.status, latency: rootRes.latency, ok: rootRes.ok });

    // 2. Iterate through each provider
    for (const prov of PROVIDERS) {
        console.log(`\n-------------------------------------------------`);
        console.log(`PROV: ${prov.name.toUpperCase()} (${prov.id})`);
        console.log(`-------------------------------------------------`);

        let dramaId = null;
        let dramaName = "";
        let episodeId = null;
        let episodeNum = 1;

        // A. Languages
        if (prov.hasLanguages) {
            const res = await testUrl(`${BASE_URL}/${prov.id}/languages`, "Languages");
            console.log(`- /languages: Status ${res.status} | Latency ${res.latency}ms`);
            report.push({ provider: prov.name, endpoint: '/languages', status: res.status, latency: res.latency, ok: res.ok });
        }

        // B. Home
        const homeRes = await testUrl(`${BASE_URL}/${prov.id}/home?lang=id`, "Home");
        console.log(`- /home: Status ${homeRes.status} | Latency ${homeRes.latency}ms`);
        report.push({ provider: prov.name, endpoint: '/home', status: homeRes.status, latency: homeRes.latency, ok: homeRes.ok });
        
        if (homeRes.ok && homeRes.data) {
            const dramas = findDramaDetails(homeRes.data);
            if (dramas.length > 0) {
                dramaId = dramas[0].id;
                dramaName = dramas[0].name;
            }
        }

        // C. New
        const newRes = await testUrl(`${BASE_URL}/${prov.id}/new?lang=id&page=1`, "New");
        console.log(`- /new: Status ${newRes.status} | Latency ${newRes.latency}ms`);
        report.push({ provider: prov.name, endpoint: '/new', status: newRes.status, latency: newRes.latency, ok: newRes.ok });

        // D. Popular
        const popRes = await testUrl(`${BASE_URL}/${prov.id}/populer?lang=id&page=1`, "Popular");
        console.log(`- /populer: Status ${popRes.status} | Latency ${popRes.latency}ms`);
        report.push({ provider: prov.name, endpoint: '/populer', status: popRes.status, latency: popRes.latency, ok: popRes.ok });

        // E. Search
        const searchQuery = prov.id === 'goodshort' ? 'SISTEM' : 'cinta';
        const searchUrl = `${BASE_URL}/${prov.id}/search?${prov.searchParam}=${searchQuery}&page=1&lang=id`;
        const searchRes = await testUrl(searchUrl, "Search");
        console.log(`- /search: Status ${searchRes.status} | Latency ${searchRes.latency}ms`);
        report.push({ provider: prov.name, endpoint: '/search', status: searchRes.status, latency: searchRes.latency, ok: searchRes.ok });

        if (!dramaId && searchRes.ok && searchRes.data) {
            const dramas = findDramaDetails(searchRes.data);
            if (dramas.length > 0) {
                dramaId = dramas[0].id;
                dramaName = dramas[0].name;
            }
        }

        // F. Detail
        if (dramaId) {
            console.log(`  > Using Drama: "${dramaName}" (ID: ${dramaId})`);
            const params = prov.detailParams(dramaId);
            const queryStr = Object.entries(params).map(([k, v]) => `${k}=${v}`).join('&');
            const detailRes = await testUrl(`${BASE_URL}/${prov.id}/detail?${queryStr}`, "Detail");
            console.log(`- /detail: Status ${detailRes.status} | Latency ${detailRes.latency}ms`);
            report.push({ provider: prov.name, endpoint: '/detail', status: detailRes.status, latency: detailRes.latency, ok: detailRes.ok });

            if (detailRes.ok && detailRes.data) {
                const eps = findEpisodeDetails(detailRes.data);
                if (eps.length > 0) {
                    episodeId = eps[0].id;
                    if (eps[0].num !== null && eps[0].num !== undefined) {
                        episodeNum = eps[0].num;
                    }
                }
            }
        } else {
            console.log(`- /detail: SKIPPED (No Drama ID found)`);
            report.push({ provider: prov.name, endpoint: '/detail', status: 'SKIPPED', latency: 0, ok: false });
        }

        // G. Stream
        if (dramaId) {
            const params = prov.streamParams(dramaId, episodeId, episodeNum);
            const queryStr = Object.entries(params).map(([k, v]) => `${k}=${v}`).join('&');
            const streamRes = await testUrl(`${BASE_URL}/${prov.id}/stream?${queryStr}`, "Stream");
            console.log(`- /stream: Status ${streamRes.status} | Latency ${streamRes.latency}ms`);
            report.push({ provider: prov.name, endpoint: '/stream', status: streamRes.status, latency: streamRes.latency, ok: streamRes.ok });
            if (streamRes.ok && streamRes.data) {
                const snippet = JSON.stringify(streamRes.data).substring(0, 100);
                console.log(`  > Stream Info: ${snippet}...`);
            }
        } else {
            console.log(`- /stream: SKIPPED (No Drama ID found)`);
            report.push({ provider: prov.name, endpoint: '/stream', status: 'SKIPPED', latency: 0, ok: false });
        }
    }

    console.log("\n=================================================");
    console.log("                TEST SUITE SUMMARY               ");
    console.log("=================================================");
    console.table(report.map(r => ({
        Provider: r.provider,
        Endpoint: r.endpoint,
        Status: r.status,
        Latency: r.latency + "ms",
        StatusCheck: r.ok ? "✅ OK" : (r.status === 'SKIPPED' ? "➖ SKIPPED" : "❌ FAILED")
    })));
}

main();
