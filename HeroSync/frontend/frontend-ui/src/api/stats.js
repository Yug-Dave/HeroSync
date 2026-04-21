import { http, extractError } from "./http";

/**
 * Returns the last 7 days of XP gain history for the authenticated user,
 * used to populate the dashboard XP chart.
 * @returns {Promise<XpHistoryDto[]>}
 * @throws {string} Human-readable error message on failure.
 */
export async function getXpHistory() {
  try {
    const res = await http.get("/stats/xp-history");
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not load XP history. Please refresh.");
  }
}
