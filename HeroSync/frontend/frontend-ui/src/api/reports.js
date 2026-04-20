import { http, extractError } from "./http";

/**
 * Returns a weekly habit report for the given ISO date string (YYYY-MM-DD).
 * The backend derives the week boundaries from this date.
 * @param {string} date - ISO date within the desired week.
 * @returns {Promise<ReportDto>}
 * @throws {string} Human-readable error message on failure.
 */
export async function getWeeklyReport(date) {
  try {
    const res = await http.get("/api/reports/weekly", { params: { date } });
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not load weekly report. Please try again.");
  }
}

/**
 * Returns a monthly habit report for the given year and month (1-indexed).
 * @param {number} year
 * @param {number} month - 1 = January … 12 = December.
 * @returns {Promise<ReportDto>}
 * @throws {string} Human-readable error message on failure.
 */
export async function getMonthlyReport(year, month) {
  try {
    const res = await http.get("/api/reports/monthly", { params: { year, month } });
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not load monthly report. Please try again.");
  }
}
