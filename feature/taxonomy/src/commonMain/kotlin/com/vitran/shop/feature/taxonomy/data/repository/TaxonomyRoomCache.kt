package com.vitran.shop.feature.taxonomy.data.repository

/**
 * Wasm/JS Room uses a web worker that can suspend forever when OPFS is unavailable.
 * A cache read on that path must not run before [com.vitran.shop.feature.taxonomy.data.remote.TaxonomyApi].
 */
internal expect val taxonomyRoomCacheMayHang: Boolean
