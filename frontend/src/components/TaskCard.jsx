import React from 'react';
import styled from 'styled-components';
import { TASK_PRIORITY, TASK_TYPE } from '../config/constants';

const Card = styled.div`
  background-color: var(--neutral-0);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-3);
  margin-bottom: var(--spacing-3);
  border-left: 3px solid ${props => {
    switch(props.priority) {
      case TASK_PRIORITY.HIGHEST: return 'var(--error)';
      case TASK_PRIORITY.HIGH: return 'var(--warning)';
      case TASK_PRIORITY.MEDIUM: return 'var(--primary)';
      case TASK_PRIORITY.LOW: return 'var(--success)';
      case TASK_PRIORITY.LOWEST: return 'var(--neutral-400)';
      default: return 'var(--primary)';
    }
  }};
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-md);
  }
`;

const TaskHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-2);
`;

const TaskTypeIcon = styled.span`
  display: inline-block;
  width: 16px;
  height: 16px;
  margin-right: var(--spacing-2);
  color: ${props => {
    switch(props.type) {
      case TASK_TYPE.BUG: return 'var(--error)';
      case TASK_TYPE.STORY: return 'var(--success)';
      case TASK_TYPE.EPIC: return 'var(--purple)';
      default: return 'var(--primary)';
    }
  }};
`;

const TaskId = styled.span`
  font-size: 12px;
  color: var(--neutral-600);
  font-family: monospace;
`;

const TaskTitle = styled.h4`
  font-size: 14px;
  font-weight: 500;
  margin: var(--spacing-1) 0 var(--spacing-3);
  color: var(--neutral-900);
  word-break: break-word;
`;

const TaskFooter = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--neutral-600);
`;

const AssigneeChip = styled.div`
  display: flex;
  align-items: center;
`;

const AssigneeAvatar = styled.div`
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: var(--primary-lightest);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 500;
  margin-right: var(--spacing-2);
`;

const getTaskIcon = (type) => {
  switch(type) {
    case TASK_TYPE.BUG: return '🐞';
    case TASK_TYPE.STORY: return '📖';
    case TASK_TYPE.EPIC: return '🌟';
    default: return '📋';
  }
};

const getPriorityText = (priority) => {
  switch(priority) {
    case TASK_PRIORITY.HIGHEST: return 'Highest';
    case TASK_PRIORITY.HIGH: return 'High';
    case TASK_PRIORITY.MEDIUM: return 'Medium';
    case TASK_PRIORITY.LOW: return 'Low';
    case TASK_PRIORITY.LOWEST: return 'Lowest';
    default: return 'Medium';
  }
};

const TaskCard = ({ task, onClick }) => {
  const getInitials = (name) => {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  };

  return (
    <Card priority={task.priority} onClick={onClick} className="card-appear">
      <TaskHeader>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <TaskTypeIcon type={task.type}>
            {getTaskIcon(task.type)}
          </TaskTypeIcon>
          <TaskId>{task.id}</TaskId>
        </div>
        <span style={{ fontSize: '12px', color: 'var(--neutral-600)' }}>
          {getPriorityText(task.priority)}
        </span>
      </TaskHeader>
      
      <TaskTitle>{task.title}</TaskTitle>
      
      <TaskFooter>
        {task.assignee ? (
          <AssigneeChip>
            <AssigneeAvatar>
              {getInitials(task.assignee.name)}
            </AssigneeAvatar>
            {task.assignee.name}
          </AssigneeChip>
        ) : (
          <span>Unassigned</span>
        )}
        
        {task.dueDate && (
          <span>Due: {new Date(task.dueDate).toLocaleDateString()}</span>
        )}
      </TaskFooter>
    </Card>
  );
};

export default TaskCard;