import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { taskAPI, projectAPI } from '../config/api';
import TaskCard from '../components/TaskCard';
import TaskDetailModal from '../components/TaskDetailModal'; // Add this import
import { toast } from 'react-toastify'; // Add this import


const PageContainer = styled.div`
  padding: var(--spacing-6);
  max-width: 1200px;
  margin: 0 auto;
`;

const StatsContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-6);
`;

const StatCard = styled.div`
  background-color: var(--neutral-0);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-4);
  box-shadow: var(--shadow-sm);
  text-align: center;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-md);
    transition: all 0.2s ease-in-out;
  }
`;

const StatNumber = styled.div`
  font-size: 32px;
  font-weight: 600;
  color: var(--primary);
  margin-bottom: var(--spacing-2);
`;

const StatLabel = styled.div`
  color: var(--neutral-600);
  font-size: 14px;
`;

const FilterDropdown = styled.select`
  padding: var(--spacing-2) var(--spacing-4);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--neutral-200);
  background-color: var(--neutral-0);
  color: var(--neutral-900);
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  
  &:hover {
    border-color: var(--primary);
  }
  
  &:focus {
    outline: none;
    border-color: var(--primary);
    box-shadow: 0 0 0 2px var(--primary-lighter);
  }
`;

const FilterContainer = styled.div`
  display: flex;
  gap: var(--spacing-2);
  margin-bottom: var(--spacing-6);
  flex-wrap: wrap;
  align-items: center;
  
  ${FilterDropdown} {
    margin-left: auto;
    min-width: 200px;
  }
`;

const FilterButton = styled.button`
  padding: var(--spacing-2) var(--spacing-4);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--neutral-200);
  background-color: ${props => props.active ? 'var(--primary)' : 'var(--neutral-0)'};
  color: ${props => props.active ? 'white' : 'var(--neutral-900)'};
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  
  &:hover {
    background-color: ${props => props.active ? 'var(--primary-dark)' : 'var(--neutral-100)'};
  }
`;

const TasksGrid = styled.div`
  display: grid;
  gap: var(--spacing-4);
`;



const MyTasks = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');
  const [selectedTask, setSelectedTask] = useState(null); // Add this state
  const [taskDetailModalOpen, setTaskDetailModalOpen] = useState(false); // Add this state
  const [projects, setProjects] = useState([]);
  const [selectedProject, setSelectedProject] = useState('ALL');
  const [stats, setStats] = useState({
    total: 0,
    todo: 0,
    inProgress: 0,
    inReview: 0,
    done: 0
  });

  
  useEffect(() => {
    const fetchData = async () => {
        try {
        setLoading(true);
        // Fetch tasks
        const tasksResponse = await taskAPI.getAssigned();
        const userTasks = tasksResponse.data;
        console.log('User Tasks:', userTasks);
        // Get unique project IDs from tasks
      const uniqueProjectIds = [...new Set(userTasks.map(task => task.projectId))];
      
      // Fetch project details for each unique project ID
      const projectPromises = uniqueProjectIds.map(projectId => 
        projectAPI.getById(projectId)
      );
      
      const projectResponses = await Promise.all(projectPromises);
      const projectsData = projectResponses.map(response => response.data);
      
      setProjects(projectsData);
        
        // Calculate stats
        const newStats = userTasks.reduce((acc, task) => {
            acc.total++;
            switch (task.status) {
            case 'TODO':
                acc.todo++;
                break;
            case 'IN_PROGRESS':
                acc.inProgress++;
                break;
            case 'IN_REVIEW':
                acc.inReview++;
                break;
            case 'DONE':
                acc.done++;
                break;
            default:
                break;
            }
            return acc;
        }, {
            total: 0,
            todo: 0,
            inProgress: 0,
            inReview: 0,
            done: 0
        });

        setStats(newStats);
        setTasks(userTasks);
        } catch (error) {
        console.error('Error fetching data:', error);
        toast.error('Failed to fetch tasks');
        } finally {
        setLoading(false);
        }
    };

    fetchData();
    }, []);

  const handleTaskUpdate = (updatedTask) => {
    setTasks(prevTasks => 
      prevTasks.map(task => task.id === updatedTask.id ? updatedTask : task)
    );

    // Update stats when task status changes
    const newStats = { ...stats };
    if (selectedTask.status !== updatedTask.status) {
      newStats[selectedTask.status.toLowerCase()]--;
      newStats[updatedTask.status.toLowerCase()]++;
      setStats(newStats);
    }
    
    toast.success('Task updated successfully');
  };

  const handleTaskDelete = async (taskId) => {
    try {
      await taskAPI.delete(taskId);
      setTasks(prevTasks => prevTasks.filter(task => task.id !== taskId));
      
      // Update stats when task is deleted
      const taskToDelete = tasks.find(task => task.id === taskId);
      if (taskToDelete) {
        setStats(prevStats => ({
          ...prevStats,
          total: prevStats.total - 1,
          [taskToDelete.status.toLowerCase()]: prevStats[taskToDelete.status.toLowerCase()] - 1
        }));
      }
      
      setTaskDetailModalOpen(false);
      setSelectedTask(null);
      toast.success('Task deleted successfully');
    } catch (error) {
      console.error('Error deleting task:', error);
      toast.error('Failed to delete task');
    }
  };

  const handleTaskClick = (task) => {
    setSelectedTask(task);
    setTaskDetailModalOpen(true);
  };

  const filteredTasks = tasks.filter(task => {
    // First filter by status
    if (filter !== 'ALL' && task.status !== filter) {
        return false;
    }
    
    // Then filter by project
    if (selectedProject !== 'ALL' && task.projectId.toString() !== selectedProject.toString()) {
        return false;
    }
    
    return true;
  });

  if (loading) {
    return (
      <PageContainer>
        <div>Loading tasks...</div>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <h1 style={{ marginBottom: 'var(--spacing-6)' }}>My Tasks</h1>
      
      <StatsContainer>
        <StatCard>
          <StatNumber>{stats.total}</StatNumber>
          <StatLabel>Total Tasks</StatLabel>
        </StatCard>
        <StatCard>
          <StatNumber>{stats.todo}</StatNumber>
          <StatLabel>To Do</StatLabel>
        </StatCard>
        <StatCard>
          <StatNumber>{stats.inProgress}</StatNumber>
          <StatLabel>In Progress</StatLabel>
        </StatCard>
        <StatCard>
          <StatNumber>{stats.inReview}</StatNumber>
          <StatLabel>In Review</StatLabel>
        </StatCard>
        <StatCard>
          <StatNumber>{stats.done}</StatNumber>
          <StatLabel>Done</StatLabel>
        </StatCard>
      </StatsContainer>

      <FilterContainer>
        <FilterButton 
          active={filter === 'ALL'} 
          onClick={() => setFilter('ALL')}
        >
          All
        </FilterButton>
        <FilterButton 
          active={filter === 'TODO'} 
          onClick={() => setFilter('TODO')}
        >
          To Do
        </FilterButton>
        <FilterButton 
          active={filter === 'IN_PROGRESS'} 
          onClick={() => setFilter('IN_PROGRESS')}
        >
          In Progress
        </FilterButton>
        <FilterButton 
          active={filter === 'IN_REVIEW'} 
          onClick={() => setFilter('IN_REVIEW')}
        >
          In Review
        </FilterButton>
        <FilterButton 
          active={filter === 'DONE'} 
          onClick={() => setFilter('DONE')}
        >
          Done
        </FilterButton>
        <FilterDropdown
            value={selectedProject}
            onChange={(e) => setSelectedProject(e.target.value)}
            >
            <option value="ALL">All Projects</option>
            {projects.map(project => (
                <option key={project.id} value={project.id}>
                {project.name}
                </option>
            ))}
        </FilterDropdown>
      </FilterContainer>

      <TasksGrid>
        {filteredTasks.length > 0 ? (
          filteredTasks.map(task => (
            <TaskCard
              task={task}
              onClick={() => handleTaskClick(task)}
            />
          ))
        ) : (
          <div style={{ 
            textAlign: 'center', 
            padding: 'var(--spacing-6)', 
            backgroundColor: 'var(--neutral-0)',
            borderRadius: 'var(--border-radius-lg)',
            color: 'var(--neutral-600)'
          }}>
            No tasks found
          </div>
        )}
      </TasksGrid>

      {taskDetailModalOpen && selectedTask && (
        <TaskDetailModal
          task={selectedTask}
          project={selectedTask.project}
          onClose={() => {
            setTaskDetailModalOpen(false);
            setSelectedTask(null);
          }}
          onUpdateTask={handleTaskUpdate}
          onTaskDelete={handleTaskDelete}
        />
      )}
    </PageContainer>
  );
};

export default MyTasks;