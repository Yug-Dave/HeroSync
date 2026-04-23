/**
 * Resolves a 3D model URL or a 2D seed into a displayable image URL.
 */
export function getAvatarImageUrl(avatarSeedOrUrl) {
  try {
    const apiBase = 'https://api.dicebear.com/7.x';
    
    if (!avatarSeedOrUrl || typeof avatarSeedOrUrl !== 'string' || avatarSeedOrUrl.trim() === '') {
      return `${apiBase}/adventurer/svg?seed=Hero`;
    }

    let seedToUse = avatarSeedOrUrl;

    if (avatarSeedOrUrl.includes('|||')) {
      const parts = avatarSeedOrUrl.split('|||');
      seedToUse = parts[1] || parts[0]; 
    }

    // Check if it's a 3D model BEFORE returning as-is
    const is3D = is3DAvatar(seedToUse);
    if (is3D) {
      // It's a 3D model (GLB or data:model), so return a robot preview
      return `https://api.dicebear.com/7.x/bottts/png?seed=RobotHero`;
    }

    // If it's a legitimate 2D image URL, return it
    if (seedToUse.startsWith('http') || seedToUse.startsWith('data:image/')) {
      return seedToUse;
    }

    const finalSeed = seedToUse.substring(0, 20) || 'Hero';
    return `${apiBase}/adventurer/svg?seed=${encodeURIComponent(finalSeed)}`;
  } catch (e) {
    return 'https://api.dicebear.com/7.x/adventurer/svg?seed=Hero';
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
