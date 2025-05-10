// API URL - change this according to your Spring Boot backend URL
export const API_URL = 'http://localhost:8082/api';

// User Roles
export const USER_ROLES = {
  MEMBER: 'MEMBER',
  PRODUCT_OWNER: 'PRODUCT_OWNER',
  SCRUM_MASTER: 'SCRUM_MASTER'
};

// Task Statuses
export const TASK_STATUS = {
  TODO: 'TODO',
  IN_PROGRESS: 'IN_PROGRESS',
  IN_REVIEW: 'IN_REVIEW',
  DONE: 'DONE'
};

// Task Priorities
export const TASK_PRIORITY = {
  HIGHEST: 'HIGHEST',
  HIGH: 'HIGH',
  MEDIUM: 'MEDIUM',
  LOW: 'LOW',
  LOWEST: 'LOWEST'
};

// Task Types
export const TASK_TYPE = {
  TASK: 'TASK',
  BUG: 'BUG',
  STORY: 'STORY',
  EPIC: 'EPIC'
};