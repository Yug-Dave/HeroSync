import { http, extractError } from "./http";

/**
 * Returns all goals for the authenticated user.
 * @returns {Promise<GoalDto[]>}
 * @throws {string} Human-readable error message on failure.
 */
export async function listGoals() {
  try {
    const res = await http.get("/api/goals");
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not load goals. Please refresh.");
  }
}

/**
 * Returns a single goal by ID.
 * @param {number} id
 * @returns {Promise<GoalDto>}
 * @throws {string} Human-readable error message on failure.
 */
export async function getGoal(id) {
  try {
    const res = await http.get(`/api/goals/${id}`);
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not load goal.");
  }
}

/**
 * Creates a new goal.
 * @param {CreateGoalRequest} payload
 * @returns {Promise<GoalDto>}
 * @throws {string} Human-readable error message on failure.
 */
export async function createGoal(payload) {
  try {
    const res = await http.post("/api/goals", payload);
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not create goal. Please try again.");
  }
}

/**
 * Manually completes a solo (non-habit-linked) goal.
 * @param {number} id
 * @returns {Promise<GoalDto>}
 * @throws {string} Human-readable error message on failure.
 */
export async function completeSoloGoal(id) {
  try {
    const res = await http.post(`/api/goals/${id}/complete`);
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not complete goal. Please try again.");
  }
}

/**
 * Updates an existing goal.
 * @param {number} id
 * @param {UpdateGoalRequest} payload
 * @returns {Promise<GoalDto>}
 * @throws {string} Human-readable error message on failure.
 */
export async function updateGoal(id, payload) {
  try {
    const res = await http.put(`/api/goals/${id}`, payload);
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not update goal. Please try again.");
  }
}

/**
 * Permanently deletes a goal.
 * @param {number} id
 * @throws {string} Human-readable error message on failure.
 */
export async function deleteGoal(id) {
  try {
    await http.delete(`/api/goals/${id}`);
  } catch (error) {
    throw extractError(error, "Could not delete goal. Please try again.");
  }
}
