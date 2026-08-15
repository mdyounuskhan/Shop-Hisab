/* Shop Hisab service worker - offline cache */
var CACHE = "shop-hisab-v1";
var FILES = [
	"./",
	"./index.html",
	"./manifest.webmanifest",
	"./icon-192.png",
	"./icon-512.png",
	"./icon-maskable-512.png"
];

self.addEventListener("install", function (event) {
	event.waitUntil(
		caches.open(CACHE).then(function (cache) {
			return cache.addAll(FILES).catch(function () {});
		}).then(function () {
			return self.skipWaiting();
		})
	);
});

self.addEventListener("activate", function (event) {
	event.waitUntil(
		caches.keys().then(function (keys) {
			return Promise.all(
				keys.filter(function (k) { return k !== CACHE; }).map(function (k) { return caches.delete(k); })
			);
		}).then(function () {
			return self.clients.claim();
		})
	);
});

self.addEventListener("fetch", function (event) {
	if (event.request.method !== "GET") return;
	event.respondWith(
		caches.match(event.request).then(function (hit) {
			if (hit) return hit;
			return fetch(event.request).then(function (res) {
				var copy = res.clone();
				caches.open(CACHE).then(function (cache) { cache.put(event.request, copy).catch(function () {}); });
				return res;
			}).catch(function () {
				return caches.match("./index.html");
			});
		})
	);
});
