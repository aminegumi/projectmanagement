import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { DragDropContext, Droppable } from 'react-beautiful-dnd';
import TaskCard from './TaskCard';
import AddTaskToSprintModal from './AddTaskToSprintModal';
import { taskAPI } from '../config/api';

const BoardContainer = styled.div`
  margin-top: var(--spacing-4);
`;

const SprintHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-4);
  background-color: var(--neutral-0);
  border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
  border: 1px solid var(--neutral-200);
`;

const SprintTitle = styled.h3`
  font-size: 18px;
  font-weight: 500;
  color: var(--neutral-900);
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
`;

const SprintStatus = styled.span`
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--border-radius-sm);
  font-size: 12px;
  font-weight: 500;
  background-color: ${props => {
    switch(props.status) {
      case 'ACTIVE': return 'var(--primary-lightest)';
      case 'COMPLETED': return 'var(--success-light)';
      default: return 'var(--neutral-200)';
    }
  }};
  color: ${props => {
    switch(props.status) {
      case 'ACTIVE': return 'var(--primary)';
      case 'COMPLETED': return 'var(--success)';
      default: return 'var(--neutral-700)';
    }
  }};
`;

const SprintInfo = styled.div`
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  font-size: 14px;
  color: var(--neutral-600);
`;

const SprintDates = styled.div`
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
`;

const TasksContainer = styled.div`
  background-color: var(--neutral-0);
  border: 1px solid var(--neutral-200);
  border-top: none;
  border-radius: 0 0 var(--border-radius-lg) var(--border-radius-lg);
  padding: var(--spacing-4);
`;

const Button = styled.button`
  padding: var(--spacing-2) var(--spacing-4);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  
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
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: var(--spacing-2);
`;

const EmptyState = styled.div`
  text-align: center;
  padding: var(--spacing-6);
  color: var(--neutral-600);
`;

const SprintBoard = ({ sprint, onStartSprint, onCompleteSprint, onUpdateTask }) => {
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isAddTaskModalOpen, setIsAddTaskModalOpen] = useState(false);
    useEffect(() => {
        fetchSprintTasks();
    }, [sprint.id]);

    const fetchSprintTasks = async () => {
      try {
          setLoading(true);
          console.log('Fetching tasks for sprint:', sprint.id);
          const response = await taskAPI.getBySprint(sprint.id);
          console.log('Received tasks:', response.data);
          setTasks(response.data);
          setError(null);
      } catch (err) {
          console.error('Error fetching sprint tasks:', err);
          console.error('Error details:', err.response?.data);
          setError('Failed to load tasks');
      } finally {
          setLoading(false);
      }
  };
    
    
    
    const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  const handleDragEnd = (result) => {
    const { destination, source, draggableId } = result;

    if (!destination) return;

    if (
      destination.droppableId === source.droppableId &&
      destination.index === source.index
    ) return;

    // Call the parent component's update function
    onUpdateTask(draggableId, destination.droppableId);
  };

  return (
    <BoardContainer>
      <SprintHeader>
        <SprintTitle>
          {sprint.name}
          <SprintStatus status={sprint.status}>{sprint.status}</SprintStatus>
        </SprintTitle>
        
        <SprintInfo>
          <SprintDates>
            <span>{formatDate(sprint.startDate)} - {formatDate(sprint.endDate)}</span>
          </SprintDates>
          
          <ButtonGroup>
            <Button onClick={() => setIsAddTaskModalOpen(true)}>
                Add Tasks
            </Button>
            
            {sprint.status === 'PLANNING' && (
              <Button primary onClick={() => onStartSprint(sprint.id)}>
                Start Sprint
              </Button>
            )}
            
            {sprint.status === 'ACTIVE' && (
              <Button onClick={() => onCompleteSprint(sprint.id)}>
                Complete Sprint
              </Button>
            )}
          </ButtonGroup>
        </SprintInfo>
      </SprintHeader>

      <DragDropContext onDragEnd={handleDragEnd}>
                <TasksContainer>
                    <Droppable droppableId={`sprint-${sprint.id}`}>
                        {(provided, snapshot) => (
                            <div
                                ref={provided.innerRef}
                                {...provided.droppableProps}
                                style={{
                                    minHeight: '100px',
                                    backgroundColor: snapshot.isDraggingOver ? 'var(--neutral-100)' : 'transparent',
                                    transition: 'background-color 0.2s'
                                }}
                            >
                                {loading ? (
                                    <div>Loading tasks...</div>
                                ) : error ? (
                                    <div>{error}</div>
                                ) : tasks.length > 0 ? (
                                    tasks.map((task, index) => (
                                        <TaskCard 
                                            key={task.id} 
                                            task={task} 
                                            index={index}
                                        />
                                    ))
                                ) : (
                                    <EmptyState>
                                        No tasks in this sprint. Click "Add Tasks" to add some tasks.
                                    </EmptyState>
                                )}
                                {provided.placeholder}
                            </div>
                        )}
                    </Droppable>
                </TasksContainer>
            </DragDropContext>

            {isAddTaskModalOpen && (
                <AddTaskToSprintModal
                    isOpen={isAddTaskModalOpen}
                    onClose={() => setIsAddTaskModalOpen(false)}
                    projectId={sprint.projectId}
                    sprintId={sprint.id}
                    onTasksAdded={() => {
                        fetchSprintTasks(); // Refresh tasks after adding new ones
                        setIsAddTaskModalOpen(false);
                    }}
                />
            )}
    </BoardContainer>
    
  );
};

export default SprintBoard;