package com.vitran.shop.ui.navigation

/**
 * Web-only: pin Compose Multiplatform resource URLs to the site root.
 *
 * Default paths are relative (`./composeResources/...`). After History pushes a
 * deep route (e.g. `/products/{id}/{slug}`), the first `stringResource` /
 * `painterResource` resolves against that path and 404s — often first seen when
 * PDP gallery hover mounts prev/next (a11y strings + chevron).
 */
expect fun initWebComposeResources()
