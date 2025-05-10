import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
// Replace this import
// import axios from 'axios';
// With these imports
import { projectAPI, taskAPI } from '../config/api';
import { toast } from 'react-toastify';

import TaskCard from '../components/TaskCard';
import { TASK_STATUS } from '../config/constants';
import CreateTaskModal from '../components/CreateTaskModal';
import TaskDetailModal from '../components/TaskDetailModal';

const BoardContainer = styled.div`
  padding: var(--spacing-4);
`;

const BoardHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
`;

const ProjectTitle = styled.h1`
  font-size: 24px;
  font-weight: 500;
`;

const BoardActionsContainer = styled.div`
  display: flex;
  gap: var(--spacing-3);
`;

const Button = styled.button`
  padding: var(--spacing-2) var(--spacing-4);
  background-color: ${props => props.primary ? 'var(--primary)' : 'white'};
  color: ${props => props.primary ? 'white' : 'var(--neutral-800)'};
  border: 1px solid ${props => props.primary ? 'var(--primary)' : 'var(--neutral-300)'};
  border-radius: var(--border-radius-md);
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    background-color: ${props => props.primary ? 'var(--primary-dark)' : 'var(--neutral-100)'};
  }
`;

const BoardColumnsContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-4);
  
  @media (max-width: 1024px) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

const BoardColumn = styled.div`
  background-color: var(--neutral-100);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-3);
  min-height: 500px;
`;

const ColumnHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-3);
  padding-bottom: var(--spacing-2);
  border-bottom: 2px solid ${props => {
    switch(props.status) {
      case TASK_STATUS.TODO: return 'var(--neutral-400)';
      case TASK_STATUS.IN_PROGRESS: return 'var(--primary)';
      case TASK_STATUS.IN_REVIEW: return 'var(--warning)';
      case TASK_STATUS.DONE: return 'var(--success)';
      default: return 'var(--neutral-400)';
    }
  }};
`;

const ColumnTitle = styled.h3`
  font-size: 16px;
  font-weight: 500;
  display: flex;
  align-items: center;
`;

const TaskCount = styled.span`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background-color: var(--neutral-200);
  color: var(--neutral-700);
  font-size: 12px;
  font-weight: 500;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  margin-left: var(--spacing-2);
`;

const TasksContainer = styled.div`
  min-height: 100px;
`;

const EmptyColumnMessage = styled.div`
  color: var(--neutral-500);
  text-align: center;
  padding: var(--spacing-6) var(--spacing-2);
  font-size: 14px;
`;

const ColumnTitleIcon = styled.span`
  margin-right: var(--spacing-2);
`;

const ProjectBoard = () => {
  const { projectId } = useParams();
  const navigate = useNavigate();
  
  const [project, setProject] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const [createTaskModalOpen, setCreateTaskModalOpen] = useState(false);
  const [taskDetailModalOpen, setTaskDetailModalOpen] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  
  // Replace mock data with real API calls
  useEffect(() => {
    const fetchProjectBoard = async () => {
      try {
        setLoading(true);
        
        // Get project details from API
        console.log(`Fetching project with ID: ${projectId}`);
        const projectResponse = await projectAPI.getById(projectId);
        const projectData = projectResponse.data;
        console.log('Project data:', projectData);
        
        // Get tasks for this project from API
        console.log(`Fetching tasks for project: ${projectId}`);
        const tasksResponse = await taskAPI.getAll(projectId);
        const tasksData = tasksResponse.data;
        console.log('Tasks data:', tasksData);
        
        setProject(projectData);
        setTasks(tasksData);
        setLoading(false);
      } catch (err) {
        console.error('Error fetching project board:', err);
        setError('Failed to load project board: ' + (err.response?.data?.message || err.message));
        setLoading(false);
      }
    };
    
    fetchProjectBoard();
  }, [projectId]);
  
  // Keep handleCreateTask as is
  const handleCreateTask = () => {
    setCreateTaskModalOpen(true);
  };
  

  const handleTaskDelete = async (taskId) => {
    try {
      setTasks(prevTasks => prevTasks.filter(task => task.id !== taskId));
      setTaskDetailModalOpen(false);
      setSelectedTask(null);
      toast.success('Task deleted successfully');
    } catch (error) {
      console.error('Error deleting task:', error);
      toast.error('Failed to delete task');
    }
  };
  // Keep handleTaskClick as is
  const handleTaskClick = (task) => {
    setSelectedTask(task);
    setTaskDetailModalOpen(true);
  };
  
  // Update handleDragEnd to use the API
  const handleDragEnd = async (result) => {
    const { destination, source, draggableId } = result;
    
    // If dropped outside a droppable area
    if (!destination) return;
    
    // If dropped in the same position
    if (
      destination.droppableId === source.droppableId &&
      destination.index === source.index
    ) return;
    
    // Clone the tasks array
    const updatedTasks = [...tasks];
    
    // Find the task that was dragged
    const taskIndex = updatedTasks.findIndex(task => task.id.toString() === draggableId);
    if (taskIndex === -1) return;
    
    const task = updatedTasks[taskIndex];
    
    // Update the task status - optimistic update
    updatedTasks[taskIndex] = {
      ...task,
      status: destination.droppableId
    };
    
    // Update the state immediately (optimistic update)
    setTasks(updatedTasks);
    
    try {
      // Make API call to update task status
      await taskAPI.updateStatus(task.id, destination.droppableId);
      toast.success('Task status updated');
    } catch (err) {
      console.error('Error updating task status:', err);
      // Revert the state if the API call fails
      setTasks([...tasks]);
      toast.error('Failed to update task status');
    }
  };
  
  // Keep the helper functions (getTasksByStatus, getColumnIcon)
  const getTasksByStatus = (status) => {
    return tasks.filter(task => task.status === status);
  };
  
  const getColumnIcon = (status) => {
    switch(status) {
      case TASK_STATUS.TODO: return '📋';
      case TASK_STATUS.IN_PROGRESS: return '🔄';
      case TASK_STATUS.IN_REVIEW: return '👀';
      case TASK_STATUS.DONE: return '✅';
      default: return '📋';
    }
  };
  
  if (loading) {
    return (
      <div style={{ padding: 'var(--spacing-10)', textAlign: 'center' }}>
        <p>Loading project board...</p>
      </div>
    );
  }
  
  if (error) {
    return (
      <div style={{ padding: 'var(--spacing-10)', textAlign: 'center', color: 'var(--error)' }}>
        <p>{error}</p>
      </div>
    );
  }
  
  if (!project) {
    return (
      <div style={{ padding: 'var(--spacing-10)', textAlign: 'center' }}>
        <p>Project not found</p>
        <Button onClick={() => navigate('/dashboard')}>Back to Dashboard</Button>
      </div>
    );
  }

  return (
    <BoardContainer>
      <BoardHeader>
        <ProjectTitle>{project.name}</ProjectTitle>
        <BoardActionsContainer>
          <Button onClick={() => navigate(`/projects/${projectId}/settings`)}>
            Project Settings
          </Button>
          <Button primary onClick={handleCreateTask}>
            Create Task
          </Button>
        </BoardActionsContainer>
      </BoardHeader>
      
      <DragDropContext onDragEnd={handleDragEnd}>
        <BoardColumnsContainer>
          {Object.values(TASK_STATUS).map(status => (
            <BoardColumn key={status}>
              <ColumnHeader status={status}>
                <ColumnTitle>
                  <ColumnTitleIcon>{getColumnIcon(status)}</ColumnTitleIcon>
                  {status.replace('_', ' ')}
                  <TaskCount>{getTasksByStatus(status).length}</TaskCount>
                </ColumnTitle>
              </ColumnHeader>
              
              <Droppable droppableId={status}>
                {(provided, snapshot) => (
                  <TasksContainer
                    ref={provided.innerRef}
                    {...provided.droppableProps}
                    style={{
                      backgroundColor: snapshot.isDraggingOver ? 'var(--neutral-200)' : 'transparent',
                      transition: 'background-color 0.2s ease',
                      borderRadius: 'var(--border-radius-md)',
                      minHeight: '400px',
                      padding: 'var(--spacing-2)'
                    }}
                  >
                    {getTasksByStatus(status).length > 0 ? (
                      getTasksByStatus(status).map((task, index) => (
                        <Draggable
                          key={task.id}
                          draggableId={task.id}
                          index={index}
                        >
                          {(provided, snapshot) => (
                            <div
                              ref={provided.innerRef}
                              {...provided.draggableProps}
                              {...provided.dragHandleProps}
                              style={{
                                ...provided.draggableProps.style,
                                transform: snapshot.isDragging
                                  ? provided.draggableProps.style.transform
                                  : 'translate(0, 0)',
                              }}
                              className={snapshot.isDragging ? 'dragging' : ''}
                            >
                              <TaskCard
                                task={task}
                                onClick={() => handleTaskClick(task)}
                              />
                            </div>
                          )}
                        </Draggable>
                      ))
                    ) : (
                      <EmptyColumnMessage>
                        No tasks in this column
                      </EmptyColumnMessage>
                    )}
                    {provided.placeholder}
                  </TasksContainer>
                )}
              </Droppable>
            </BoardColumn>
          ))}
        </BoardColumnsContainer>
      </DragDropContext>
      
      {createTaskModalOpen && (
        <CreateTaskModal
          project={project}
          onClose={() => setCreateTaskModalOpen(false)}
          onCreateTask={(newTask) => {
            // Add the new task to the tasks array
            setTasks([...tasks, newTask]);
            setCreateTaskModalOpen(false);
            toast.success('Task created successfully');
          }}
        />
      )}
      
      {/* Update TaskDetailModal to handle real API responses */}
      {taskDetailModalOpen && selectedTask && (
        <TaskDetailModal
          task={selectedTask}
          project={project}
          onClose={() => {
            setTaskDetailModalOpen(false);
            setSelectedTask(null);
          }}
          onUpdateTask={(updatedTask) => {
            setTasks(tasks.map(task => 
              task.id === updatedTask.id ? updatedTask : task
            ));
            toast.success('Task updated successfully');
          }}
          onTaskDelete={handleTaskDelete}
        />
      )}
    </BoardContainer>
  );
};

export default ProjectBoard;