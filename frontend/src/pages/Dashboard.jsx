import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom'; // Added useLocation
import styled from 'styled-components';
// Remove these imports
// import axios from 'axios';
// import { API_URL } from '../config/constants';
// Add these imports
import { projectAPI, taskAPI } from '../config/api';
import ProjectCard from '../components/ProjectCard';
import { toast } from 'react-toastify';

const PageTitle = styled.h1`
  font-size: 24px;
  font-weight: 500;
  margin-bottom: var(--spacing-6);
  color: var(--neutral-900);
`;

const DashboardContainer = styled.div`
  max-width: 1200px;
  margin: 0 auto;
`;

const DashboardHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
`;

const CreateButton = styled(Link)`
  display: inline-flex;
  align-items: center;
  background-color: var(--primary);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  padding: var(--spacing-2) var(--spacing-4);
  cursor: pointer;
  font-weight: 500;
  text-decoration: none;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: var(--primary-dark);
    text-decoration: none;
  }
`;

const TaskTable = styled.table`
  width: 100%;
  border-collapse: collapse;
  background-color: var(--neutral-0);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
`;

const TableHeader = styled.th`
  text-align: left;
  padding: var(--spacing-3) var(--spacing-4);
  border-bottom: 2px solid var(--neutral-200);
  color: var(--neutral-700);
  font-weight: 500;
`;

const TableCell = styled.td`
  padding: var(--spacing-3) var(--spacing-4);
  border-bottom: 1px solid var(--neutral-200);
  vertical-align: top;
`;

const ProjectName = styled.div`
  font-weight: 500;
  color: var(--neutral-900);
  margin-bottom: var(--spacing-2);
`;

const TaskRow = styled.tr`
  &:hover {
    background-color: var(--neutral-50);
  }
`;

const ProjectGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-4);
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

const NoProjectsMessage = styled.div`
  text-align: center;
  padding: var(--spacing-10);
  background-color: var(--neutral-0);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
`;

const TasksSection = styled.div`
  margin-top: var(--spacing-8);
`;

const TasksList = styled.div`
  background-color: var(--neutral-0);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-4);
`;

const TaskItem = styled(Link)`
  display: flex;
  align-items: center;
  padding: var(--spacing-3);
  border-bottom: 1px solid var(--neutral-200);
  text-decoration: none;
  color: inherit;
  transition: background-color 0.2s;
  
  &:last-child {
    border-bottom: none;
  }
  
  &:hover {
    background-color: var(--neutral-100);
    text-decoration: none;
  }
`;

const LoadingMessage = styled.div`
  padding: var(--spacing-4);
  text-align: center;
  color: var(--neutral-600);
  background-color: var(--neutral-0);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
`;

const TaskStatus = styled.span`
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: var(--spacing-3);
  background-color: ${props => {
    switch(props.status) {
      case 'TODO': return 'var(--neutral-400)';
      case 'IN_PROGRESS': return 'var(--primary)';
      case 'IN_REVIEW': return 'var(--warning)';
      case 'DONE': return 'var(--success)';
      default: return 'var(--neutral-400)';
    }
  }};
`;

const TaskTitle = styled.span`
  flex: 1;
  font-weight: 500;
`;

const TaskProject = styled.span`
  color: var(--neutral-600);
  font-size: 12px;
  margin-right: var(--spacing-4);
`;

const Dashboard = () => {
  const [projects, setProjects] = useState([]);
  const [recentTasks, setRecentTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const location = useLocation(); // This was missing or causing errors
  
  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        
        // Fetch projects first
        const projectsResponse = await projectAPI.getAll();
        const projects = projectsResponse.data;
        setProjects(projects);
        
        // Fetch tasks
        const tasksResponse = await taskAPI.getAssigned();
        
        // Add project information to each task
        const tasksWithProjects = tasksResponse.data.map(task => ({
          ...task,
          project: {
            id: task.projectId,
            name: task.projectName
          }
        }));
        
        setRecentTasks(tasksWithProjects);
        setLoading(false);
      } catch (err) {
        console.error('Error fetching dashboard data:', err);
        setError('Failed to load dashboard data: ' + (err.response?.data?.message || err.message));
        setLoading(false);
      }
    };
  
    fetchDashboardData();
  }, [location]);

  if (loading) {
    return (
      <div style={{ padding: 'var(--spacing-10)', textAlign: 'center' }}>
        <p>Loading dashboard...</p>
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

  return (
    <DashboardContainer>
      <DashboardHeader>
        <PageTitle>My Projects</PageTitle>
        <CreateButton to="/projects/create">+ Create Project</CreateButton>
      </DashboardHeader>
      
      {projects.length > 0 ? (
        <ProjectGrid>
          {projects.map(project => (
            <ProjectCard key={project.id} project={project} />
          ))}
        </ProjectGrid>
      ) : (
        <NoProjectsMessage>
          <h3 style={{ marginBottom: 'var(--spacing-4)' }}>No projects yet</h3>
          <p style={{ marginBottom: 'var(--spacing-6)', color: 'var(--neutral-600)' }}>
            Create your first project to get started with task management
          </p>
          <CreateButton to="/projects/create">+ Create Project</CreateButton>
        </NoProjectsMessage>
      )}
      
      <TasksSection>
        <h2 style={{ marginBottom: 'var(--spacing-4)' }}>My Recent Tasks</h2>
        {loading ? (
          <LoadingMessage>
            Loading tasks...
          </LoadingMessage>
        ) : recentTasks.length > 0 ? (
                  <TaskTable>
                    <thead>
                      <tr>
                        <TableHeader>Project</TableHeader>
                        <TableHeader>Tasks</TableHeader>
                        <TableHeader>Status</TableHeader>
                        <TableHeader>Key</TableHeader>
                      </tr>
                    </thead>
                    <tbody>
                    {Object.entries(
                      recentTasks.reduce((acc, task) => {
                        // Create project object from task data
                        const project = {
                          id: task.projectId,
                          name: task.projectName
                        };
                        
                        // Add project to task if it doesn't exist
                        const taskWithProject = {
                          ...task,
                          project: task.project || project
                        };
                        
                        // Initialize project entry if it doesn't exist
                        if (!acc[project.id]) {
                          acc[project.id] = {
                            project,
                            tasks: []
                          };
                        }
                        
                        // Add task to project's task list
                        acc[project.id].tasks.push(taskWithProject);
                        return acc;
                      }, {})
                    ).map(([projectId, { project, tasks }]) => (
                        <TaskRow key={projectId}>
                          <TableCell>
                            <Link to={`/projects/${project.id}`} style={{ textDecoration: 'none' }}>
                              <ProjectName>{project.name || 'Unnamed Project'}</ProjectName>
                            </Link>
                          </TableCell>
                          <TableCell>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--spacing-2)' }}>
                              {tasks.map(task => (
                                <Link
                                  key={task.id}
                                  to={`/projects/${project.id}?taskId=${task.id}`}
                                  style={{ 
                                    textDecoration: 'none',
                                    color: 'var(--neutral-800)',
                                    display: 'block',
                                    padding: 'var(--spacing-2)'
                                  }}
                                >
                                  {task.title || 'Untitled Task'}
                                </Link>
                              ))}
                            </div>
                          </TableCell>
                          <TableCell>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--spacing-2)' }}>
                              {tasks.map(task => (
                                <div key={task.id} style={{ display: 'flex', alignItems: 'center', padding: 'var(--spacing-2)' }}>
                                  <TaskStatus status={task.status || 'TODO'} />
                                  {(task.status || 'TODO').replace('_', ' ')}
                                </div>
                              ))}
                            </div>
                          </TableCell>
                          <TableCell>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--spacing-2)' }}>
                              {tasks.map(task => (
                                <span key={task.id} style={{ 
                                  color: 'var(--neutral-600)', 
                                  fontSize: '12px',
                                  padding: 'var(--spacing-2)'
                                }}>
                                  {task.key || '-'}
                                </span>
                              ))}
                            </div>
                          </TableCell>
                        </TaskRow>
                      ))}
                    </tbody>
                  </TaskTable>
                    ) : (
                      <div style={{ 
                        padding: 'var(--spacing-4)', 
                        textAlign: 'center', 
                        color: 'var(--neutral-600)',
                        backgroundColor: 'var(--neutral-0)',
                        borderRadius: 'var(--border-radius-lg)',
                        boxShadow: 'var(--shadow-sm)'
                      }}>
                        No tasks assigned to you
                      </div>
                    )}
      </TasksSection>
    </DashboardContainer>
  );
};

export default Dashboard;