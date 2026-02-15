const CATEGORY_CLUB_MAP = {
  Technical: "ByteForge",
  ArtsAndCulture: "ArtVerse",
  Photography: "The Viewfinder",
  Sports: "Champions Circle",
};

export const CLUB_CATEGORY_MAP = Object.entries(CATEGORY_CLUB_MAP).reduce(
  (acc, [category, clubName]) => {
    acc[clubName.toUpperCase()] = category;
    return acc;
  },
  {}
);

// export const getCategoryName = (category) => {
//   return CATEGORY_MAP[category] || category || 'General';
// };

export const getClubName = (category) => {
  if (!category) return 'General Club';

  return (
    CATEGORY_CLUB_MAP[category.toUpperCase()] ||
    'General Club'
  );
};

/**
 * Club name → Category
 * Example: 'Manchathantra Club' → 'DRAMA'
 */
export const getCategoryName = (clubName) => {
  if (!clubName) return 'GENERAL';

  return (
    CLUB_CATEGORY_MAP[clubName.toUpperCase()] ||
    'GENERAL'
  );
};