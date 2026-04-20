/**
 * Resolves a 3D model URL or a 2D seed into a displayable image URL.
 */
export function getAvatarImageUrl(avatarSeedOrUrl) {
  try {
    if (!avatarSeedOrUrl || typeof avatarSeedOrUrl !== 'string') {
      return 'https://api.dicebear.com/9.x/adventurer/svg?seed=Hero&backgroundColor=transparent';
    }

    const effectiveUrl = expandAvatarUrl(avatarSeedOrUrl);

    // Support for the new 'Combo' string format: 3D_DATA|||THUMBNAIL_URL
    if (effectiveUrl.includes('|||')) {
      const parts = effectiveUrl.split('|||');
      return parts[1] || parts[0]; // Return the thumbnail part
    }

    // Support for Ready Player Me portraits
    if (effectiveUrl.includes('readyplayer.me') && effectiveUrl.includes('.glb')) {
      return effectiveUrl.split('?')[0].replace('.glb', '.png') + '?size=256&camera=portrait';
    }

    // Support for 3D URLs that include a 2D thumbnail (common in Avaturn)
    const imageUrlMatch = effectiveUrl.match(/image=([^&]+)/);
    if (imageUrlMatch && imageUrlMatch[1]) {
      return decodeURIComponent(imageUrlMatch[1]);
    }

    // Fallback for 3D GLB models or Data URLs
    if (is3DAvatar(effectiveUrl)) {
      // Use a clean seed for the 2D fallback (Robot style for 3D)
      return `https://api.dicebear.com/9.x/bottts-neutral/svg?seed=${encodeURIComponent(effectiveUrl.substring(0, 10))}&backgroundColor=transparent`;
    }

    return `https://api.dicebear.com/9.x/adventurer/svg?seed=${encodeURIComponent(avatarSeedOrUrl.substring(0, 20))}&backgroundColor=transparent`;
  } catch (e) {
    return 'https://api.dicebear.com/9.x/adventurer/svg?seed=Hero&backgroundColor=transparent';
  }
}

/**
 * Shortens long HTTP URLs, but leaves Data URLs as-is
 */
export function compressAvatarUrl(url) {
  if (typeof url !== 'string') return url;
  if (url.startsWith('data:')) return url;
  if (url.includes('.avaturn.dev/')) {
    const match = url.match(/https?:\/\/([^/]+)\/(.+)/);
    if (match) return `avaturn://${match[1]}/${match[2]}`;
  }
  return url;
}

/**
 * Expands a shortened URL back to a full URL.
 */
export function expandAvatarUrl(url) {
  if (typeof url !== 'string') return url;
  let expanded = url;
  if (url.startsWith('avaturn://')) {
    expanded = 'https://' + url.replace('avaturn://', '');
  }
  // If it's a combo string, extract only the 3D part for the viewer
  if (expanded.includes('|||')) {
    return expanded.split('|||')[0];
  }
  return expanded;
}

/**
 * Returns true if the given avatar string is a 3D model.
 */
export function is3DAvatar(avatarSeedOrUrl) {
  if (typeof avatarSeedOrUrl !== 'string') return false;
  // Very permissive check: if it has .glb or starts with data: it's 3D
  return (
    avatarSeedOrUrl.includes('.glb') ||
    avatarSeedOrUrl.startsWith('avaturn://') ||
    avatarSeedOrUrl.includes('data:model/') ||
    avatarSeedOrUrl.startsWith('data:application/octet-stream') ||
    (avatarSeedOrUrl.startsWith('data:') && avatarSeedOrUrl.length > 1000)
  );
}
