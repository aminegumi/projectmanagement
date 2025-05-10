import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { sprintAPI, taskAPI } from '../config/api';
import { id } from 'date-fns/locale';

const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(9, 30, 66, 0.54);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: var(--z-modal);
`;

const ModalContent = styled.div`
  background-color: var(--neutral-0);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-xl);
  width: 100%;
  max-width: 600px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
`;

const ModalHeader = styled.div`
  padding: var(--spacing-4);
  border-bottom: 1px solid var(--neutral-200);
`;

const ModalTitle = styled.h2`
  font-size: 20px;
  font-weight: 500;
  color: var(--neutral-900);
`;

const TaskList = styled.div`
  padding: var(--spacing-4);
  overflow-y: auto;
  max-height: 60vh;
`;

const TaskItem = styled.div`
  display: flex;
  align-items: center;
  padding: var(--spacing-3);
  border: 1px solid var(--neutral-200);
  border-radius: var(--border-radius-md);
  margin-bottom: var(--spacing-2);
  
  &:hover {
    background-color: var(--neutral-50);
  }
`;

const TaskCheckbox = styled.input`
  margin-right: var(--spacing-3);
`;

const TaskInfo = styled.div`
  flex: 1;
`;

const TaskTitle = styled.div`
  font-weight: 500;
  color: var(--neutral-900);
`;

const TaskDescription = styled.div`
  font-size: 14px;
  color: var(--neutral-600);
  margin-top: var(--spacing-1);
`;

const ModalFooter = styled.div`
  padding: var(--spacing-4);
  border-top: 1px solid var(--neutral-200);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-3);
`;

const Button = styled.button`
  padding: var(--spacing-2) var(--spacing-4);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  
  ${props => props.primary ? `
    background-color: var(--primary);
    color: white;
    border: none;
    
    &:hover {
      background-color: var(--primary-dark);
    }
  ` : `
    background-color: var(--neutral-0);
    color: var(--neutral-700);
    border: 1px solid var(--neutral-300);
    
    &:hover {
      background-color: var(--neutral-100);
    }
  `}
`;

const NoTasksMessage = styled.div`
  text-align: center;
  padding: var(--spacing-6);
  color: var(--neutral-600);
`;

const AddTaskToSprintModal = ({ isOpen, onClose, projectId, sprintId, onTasksAdded }) => {
  const [tasks, setTasks] = useState([]);
  const [selectedTasks, setSelectedTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchAvailableTasks();
  }, [projectId]);

  const fetchAvailableTasks = async () => {
    try {
      const response = await taskAPI.getAll(projectId);
      // Filter tasks that are TO_DO and not in any sprint
      const availableTasks = response.data.filter(task => 
        task.status === 'TODO' && !task.sprintId
      );
      setTasks(availableTasks);
      setLoading(false);
    } catch (err) {
      console.error('Error fetching tasks:', err);
      setError('Failed to load tasks');
      setLoading(false);
    }
  };

  const handleTaskSelection = (taskId) => {
    setSelectedTasks(prev => {
      if (prev.includes(taskId)) {
        return prev.filter(id => id !== taskId);
      }
      return [...prev, taskId];
    });
  };

  const handleSubmit = async () => {
    try {
      const updatedTasks = await Promise.all(selectedTasks.map(async taskId => {
        try {

          const sprintResponse = await sprintAPI.getById(sprintId);
          const currentSprint = sprintResponse.data;

          const response = await taskAPI.getById(taskId);
          const task = response.data;
          
          const updatePayload = {
            ...task,
            sprint : currentSprint
          };
          console.log("chno knsift : " , updatePayload)
          const updateResponse = await taskAPI.update(taskId, updatePayload);
          return updateResponse.data;
        } catch (taskError) {
          console.error(`Error updating task ${taskId}:`, taskError);
          throw taskError;
        }
      }));
      
      onTasksAdded(updatedTasks);
      onClose();
    } catch (err) {
      console.error('Error adding tasks to sprint:', err);
      setError('Failed to add tasks to sprint');
    }
  };

  if (!isOpen) return null;

  return (
    <ModalOverlay onClick={onClose}>
      <ModalContent onClick={e => e.stopPropagation()}>
        <ModalHeader>
          <ModalTitle>Add Tasks to Sprint</ModalTitle>
        </ModalHeader>
        
        <TaskList>
          {loading ? (
            <div>Loading tasks...</div>
          ) : error ? (
            <div>{error}</div>
          ) : tasks.length > 0 ? (
            tasks.map(task => (
              <TaskItem key={task.id}>
                <TaskCheckbox
                  type="checkbox"
                  checked={selectedTasks.includes(task.id)}
                  onChange={() => handleTaskSelection(task.id)}
                />
                <TaskInfo>
                  <TaskTitle>{task.title}</TaskTitle>
                  <TaskDescription>{task.description}</TaskDescription>
                </TaskInfo>
              </TaskItem>
            ))
          ) : (
            <NoTasksMessage>
              No available tasks to add to this sprint.
            </NoTasksMessage>
          )}
        </TaskList>

        <ModalFooter>
          <Button onClick={onClose}>Cancel</Button>
          <Button 
            primary 
            onClick={handleSubmit}
            disabled={selectedTasks.length === 0}
          >
            Add Selected Tasks
          </Button>
        </ModalFooter>
      </ModalContent>
    </ModalOverlay>
  );
};

export default AddTaskToSprintModal;