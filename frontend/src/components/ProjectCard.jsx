import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';
import { taskAPI , projectAPI} from '../config/api';

const Card = styled(Link)`
  display: block;
  background-color: var(--neutral-0);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-4);
  transition: transform 0.2s, box-shadow 0.2s;
  text-decoration: none;
  color: inherit;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
    text-decoration: none;
  }
`;

const ProjectHeader = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: var(--spacing-4);
`;

const ProjectIcon = styled.div`
  width: 40px;
  height: 40px;
  border-radius: var(--border-radius-md);
  background-color: ${props => props.color || 'var(--primary-lightest)'};
  color: ${props => props.textColor || 'var(--primary)'};
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 500;
  margin-right: var(--spacing-3);
`;

const ProjectTitle = styled.h3`
  font-size: 16px;
  font-weight: 500;
  margin: 0;
  color: var(--neutral-900);
`;

const ProjectKey = styled.span`
  font-size: 12px;
  color: var(--neutral-500);
  display: block;
  margin-top: 2px;
`;

const ProjectStats = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: var(--spacing-4);
  padding-top: var(--spacing-4);
  border-top: 1px solid var(--neutral-200);
`;

const StatItem = styled.div`
  text-align: center;
`;

const StatValue = styled.div`
  font-size: 18px;
  font-weight: 500;
  color: var(--neutral-900);
`;

const StatLabel = styled.div`
  font-size: 12px;
  color: var(--neutral-600);
`;

const ProjectCard = ({ project }) => {
  const [taskStats, setTaskStats] = useState({
    total: 0,
    completed: 0
  });
  const [memberCount, setMemberCount] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchTaskStats = async () => {
      try {
        const response = await taskAPI.getAll(project.id);
        const tasks = response.data;
        
        const total = tasks.length;
        const completed = tasks.filter(task => task.status === 'DONE').length;
        const completedPercentage = total > 0 ? Math.round((completed / total) * 100) : 0;
        
        setTaskStats({
          total,
          completed,
          completedPercentage
        });
        // Fetch member count
        const memberResponse = await projectAPI.getMembers(project.id);
        setMemberCount(memberResponse.data.length);
      } catch (error) {
        console.error('Error fetching task stats:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchTaskStats();
  }, [project.id]);
  
  const getInitial = (name) => {
    return name.charAt(0).toUpperCase();
  };
  
  // Generate a consistent color based on project name
  const getProjectColor = (name) => {
    const colors = [
      { bg: 'var(--primary-lightest)', text: 'var(--primary)' },
      { bg: '#E3FCEF', text: 'var(--success)' },
      { bg: '#FFF7D6', text: 'var(--warning)' },
      { bg: '#FFE2DD', text: 'var(--error)' },
      { bg: '#EAE6FF', text: 'var(--purple)' }
    ];
    
    const index = name.length % colors.length;
    return colors[index];
  };
  
  const color = getProjectColor(project.name);

  return (
    <Card to={`/projects/${project.id}`} className="card-appear">
      <ProjectHeader>
        <ProjectIcon color={color.bg} textColor={color.text}>
          {getInitial(project.name)}
        </ProjectIcon>
        <div>
          <ProjectTitle>{project.name}</ProjectTitle>
          <ProjectKey>{project.key}</ProjectKey>
        </div>
      </ProjectHeader>
      
      <div>
        <p style={{ color: 'var(--neutral-700)', fontSize: '14px', margin: '0 0 var(--spacing-4)' }}>
          {project.description || 'No description provided.'}
        </p>
      </div>
      
      <ProjectStats>
        <StatItem>
          <StatValue>
            {loading ? '...' : taskStats.total}
          </StatValue>
          <StatLabel>Tasks</StatLabel>
        </StatItem>
        <StatItem>
          <StatValue>{loading ? '...' : memberCount}</StatValue>
          <StatLabel>Members</StatLabel>
        </StatItem>
        <StatItem>
          <StatValue>
            {loading ? '...' : `${taskStats.completedPercentage}%`}
          </StatValue>
          <StatLabel>Completed</StatLabel>
        </StatItem>
      </ProjectStats>
    </Card>
  );
};

export default ProjectCard;