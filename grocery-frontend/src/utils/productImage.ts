/** Served from `public/` — no external network, works offline / behind firewall */
export const PRODUCT_IMAGE_PLACEHOLDER = '/placeholder-product.svg';

/**
 * Backend may return a filename (e.g. product_bamboo_towel.png), a path, or a full URL.
 */
export function resolveProductImageSrc(imageUrl: string | null | undefined): string {
  if (!imageUrl || !imageUrl.trim()) {
    return PRODUCT_IMAGE_PLACEHOLDER;
  }
  const u = imageUrl.trim();
  if (u.startsWith('http://') || u.startsWith('https://')) {
    return u;
  }
  if (u.startsWith('/')) {
    return u;
  }
  return `/images/${u}`;
}

export function onProductImageError(e: { currentTarget: HTMLImageElement }): void {
  const el = e.currentTarget;
  if (el.src.includes('placeholder-product.svg')) {
    return;
  }
  el.src = PRODUCT_IMAGE_PLACEHOLDER;
}
