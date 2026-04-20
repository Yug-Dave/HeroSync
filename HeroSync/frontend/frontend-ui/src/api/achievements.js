import { http, extractError } from "./http";

/**
 * Returns all achievement definitions (both locked and unlocked).
 * @returns {Promise<AchievementDto[]>}
 * @throws {string} Human-readable error message on failure.
 */
export async function getAllAchievements() {
  try {
    const res = await http.get("/api/achievements");
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not load achievements. Please refresh.");
  }
}

/**
 * Returns only the achievements that the current user has unlocked.
 * @returns {Promise<UnlockedAchievementDto[]>}
 * @throws {string} Human-readable error message on failure.
 */
export async function getUnlockedAchievements() {
  try {
    const res = await http.get("/api/achievements/unlocked");
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not load unlocked achievements.");
  }
}

/**
 * Triggers server-side evaluation of achievements for the current user.
 * Newly unlocked achievements are returned in the response.
 * @returns {Promise<UnlockedAchievementDto[]>}
 * @throws {string} Human-readable error message on failure.
 */
export async function evaluateAchievements() {
  try {
    const res = await http.post("/api/achievements/evaluate");
    return res.data;
  } catch (error) {
    throw extractError(error, "Achievement evaluation failed. Please try again.");
  }
}
