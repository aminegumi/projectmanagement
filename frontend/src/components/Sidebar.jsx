import React, { useState, useEffect } from 'react';
import { NavLink, Link } from 'react-router-dom';
import styled from 'styled-components';
import { projectAPI } from '../config/api'; // Import the API

// Inside your Sidebar component, enhance the project navigation with a dropdown or context menu

// Add this styled component near your other styled components
const ProjectNavItem = styled.div`
  position: relative;
`;

const ProjectLink = styled(NavLink)`
  display: flex;
  align-items: center;
  padding: var(--spacing-2) var(--spacing-4);
  color: var(--neutral-300);
  text-decoration: none;
  font-weight: 400;
  transition: background-color 0.2s, color 0.2s;
  margin: 2px 0;
  
  &:hover {
    background-color: rgba(255, 255, 255, 0.05);
    color: white;
  }
  
  &.active {
    background-color: rgba(255, 255, 255, 0.1);
    color: white;
    font-weight: 500;
    
    &::before {
      content: '';
      position: absolute;
      left: 0;
      height: 100%;
      width: 3px;
      background-color: var(--primary-light);
    }
  }
`;

const ProjectActions = styled.div`
  padding-left: var(--spacing-6);
  margin-top: 2px;
`;

const ProjectActionLink = styled(NavLink)`
  display: flex;
  align-items: center;
  padding: var(--spacing-1) var(--spacing-4);
  color: var(--neutral-400);
  text-decoration: none;
  font-size: 13px;
  transition: color 0.2s;
  
  &:hover {
    color: white;
  }
  
  &.active {
    color: var(--primary-light);
    font-weight: 500;
  }
`;



const SidebarContainer = styled.aside`
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 260px;
  background-color: var(--neutral-800);
  color: var(--neutral-200);
  transition: transform 0.3s ease;
  z-index: var(--z-sticky);
  
  @media (max-width: 768px) {
    transform: ${({ isOpen }) => isOpen ? 'translateX(0)' : 'translateX(-100%)'};
  }
`;

const SidebarHeader = styled.div`
  padding: var(--spacing-4);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
`;

const Logo = styled(Link)`
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: 500;
  color: white;
  text-decoration: none;
  
  &:hover {
    text-decoration: none;
  }
`;

const SidebarContent = styled.div`
  padding: var(--spacing-4) 0;
  height: calc(100% - 60px);
  overflow-y: auto;
`;

const NavSection = styled.div`
  margin-bottom: var(--spacing-6);
`;

const SectionTitle = styled.h3`
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  color: var(--neutral-400);
  padding: 0 var(--spacing-4) var(--spacing-2);
  margin-bottom: var(--spacing-2);
`;

const NavItem = styled(NavLink)`
  display: flex;
  align-items: center;
  padding: var(--spacing-2) var(--spacing-4);
  color: var(--neutral-300);
  text-decoration: none;
  font-weight: 400;
  transition: background-color 0.2s, color 0.2s;
  margin: 2px 0;
  
  &:hover {
    background-color: rgba(255, 255, 255, 0.05);
    color: white;
  }
  
  &.active {
    background-color: rgba(255, 255, 255, 0.1);
    color: white;
    font-weight: 500;
    
    &::before {
      content: '';
      position: absolute;
      left: 0;
      height: 100%;
      width: 3px;
      background-color: var(--primary-light);
    }
  }
`;

const IconSpan = styled.span`
  margin-right: var(--spacing-3);
  font-size: 18px;
  display: flex;
  align-items: center;
`;

const RecentProjectsList = styled.div`
  margin-top: var(--spacing-2);
`;

const Sidebar = ({ isOpen, onToggle, user }) => {
  
  // Replace mock data with state for actual projects
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Fetch real projects when component mounts
  useEffect(() => {
    const fetchProjects = async () => {
      try {
        setLoading(true);
        console.log('Fetching projects for sidebar...');
        const response = await projectAPI.getAll();
        console.log('Sidebar projects response:', response.data);
        
        // Sort projects by name (or another criteria)
        const sortedProjects = [...response.data].sort((a, b) => 
          a.name.localeCompare(b.name)
        );
        
        setProjects(sortedProjects);
      } catch (error) {
        console.error('Error fetching projects for sidebar:', error);
      } finally {
        setLoading(false);
      }
    };
    
    fetchProjects();
  }, []);

  return (
    <SidebarContainer isOpen={isOpen}>
      <SidebarHeader>
        <Logo to="/dashboard">
          <svg 
            width="24" 
            height="24" 
            viewBox="0 0 24 24" 
            fill="none" 
            xmlns="http://www.w3.org/2000/svg"
            style={{ marginRight: '8px' }}
          >
            <path 
              d="M12 2L2 7L12 12L22 7L12 2Z" 
              fill="#4C9AFF"
            />
            <path 
              d="M2 17L12 22L22 17" 
              stroke="#4C9AFF" 
              strokeWidth="2" 
              strokeLinecap="round" 
              strokeLinejoin="round"
            />
            <path 
              d="M2 12L12 17L22 12" 
              stroke="#4C9AFF" 
              strokeWidth="2" 
              strokeLinecap="round" 
              strokeLinejoin="round"
            />
          </svg>
          TaskFlow
        </Logo>
      </SidebarHeader>
      
      <SidebarContent>
        <NavSection>
          <NavItem to="/dashboard">
            <IconSpan>📊</IconSpan>
            Dashboard
          </NavItem>
          <NavItem to="/mytasks">
            <IconSpan>✓</IconSpan>
            My Tasks
          </NavItem>
          <NavItem to="/calendar">
            <IconSpan>📅</IconSpan>
            Calendar
          </NavItem>
          <NavItem to="/reporting">
            <IconSpan>🤖</IconSpan>
            Reporting AI
          </NavItem>
        </NavSection>
        
        <NavSection>
          <SectionTitle>My Projects</SectionTitle>
          <RecentProjectsList>
          
{loading ? (
  <div style={{ padding: '0 var(--spacing-4)', color: 'var(--neutral-400)', fontSize: '14px' }}>
    Loading projects...
  </div>
) : projects.length > 0 ? (
  projects.map(project => (
    <ProjectNavItem key={project.id}>
      <ProjectLink to={`/projects/${project.id}`}>
        <IconSpan>📁</IconSpan>
        <span style={{ 
          overflow: 'hidden', 
          textOverflow: 'ellipsis', 
          whiteSpace: 'nowrap' 
        }}>
          {project.name}
        </span>
      </ProjectLink>
      <ProjectActions>
        <ProjectActionLink to={`/projects/${project.id}/settings`}>
          <IconSpan style={{ fontSize: '14px' }}>⚙️</IconSpan>
          Settings
        </ProjectActionLink>
        <ProjectActionLink to={`/projects/${project.id}/sprints`}>
          <IconSpan style={{ fontSize: '14px' }}>🏃</IconSpan>
          Sprints
        </ProjectActionLink>
      </ProjectActions>
    </ProjectNavItem>
  ))
) : (
  <div style={{ padding: '0 var(--spacing-4)', color: 'var(--neutral-400)', fontSize: '14px' }}>
    No projects found
  </div>
)}
          </RecentProjectsList>
          
          <NavItem to="/projects/create" style={{ marginTop: 'var(--spacing-2)' }}>
            <IconSpan>+</IconSpan>
            Create Project
          </NavItem>
        </NavSection>
      </SidebarContent>
    </SidebarContainer>
  );
};

export default Sidebar;