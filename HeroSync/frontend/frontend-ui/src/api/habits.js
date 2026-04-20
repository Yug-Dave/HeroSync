import { http, extractError } from "./http";

/**
 * Returns all habits for the authenticated user.
 * @returns {Promise<HabitDto[]>}
 * @throws {string} Human-readable error message on failure.
 */
export async function listHabits() {
  try {
    const res = await http.get("/api/habits");
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not load habits. Please refresh.");
  }
}

/**
 * Creates a new habit.
 * @param {{ name: string, description?: string, xpValue?: number }} payload
 * @returns {Promise<HabitDto>}
 * @throws {string} Human-readable error message on failure.
 */
export async function createHabit(payload) {
  try {
    const res = await http.post("/api/habits", payload);
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not create habit. Please try again.");
  }
}

/**
 * Updates an existing habit (partial update – omitted fields retain current values).
 * @param {number} id
 * @param {{ name?: string, description?: string, archived?: boolean, xpValue?: number }} payload
 * @returns {Promise<HabitDto>}
 * @throws {string} Human-readable error message on failure.
 */
export async function updateHabit(id, payload) {
  try {
    const res = await http.put(`/api/habits/${id}`, payload);
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not update habit. Please try again.");
  }
}

/**
 * Permanently deletes a habit and its associated activities.
 * @param {number} id
 * @throws {string} Human-readable error message on failure.
 */
export async function deleteHabit(id) {
  try {
    await http.delete(`/api/habits/${id}`);
  } catch (error) {
    throw extractError(error, "Could not delete habit. Please try again.");
  }
}
