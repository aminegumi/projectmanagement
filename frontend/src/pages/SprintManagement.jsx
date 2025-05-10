import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import styled from 'styled-components';
import SprintBoard from '../components/SprintBoard';
import CreateSprintModal from '../components/CreateSprintModal';
import { sprintAPI, taskAPI } from '../config/api';

const PageContainer = styled.div`
  padding: var(--spacing-4);
`;

const PageHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
`;

const PageTitle = styled.h1`
  font-size: 24px;
  font-weight: 500;
  color: var(--neutral-900);
`;

const Button = styled.button`
  padding: var(--spacing-2) var(--spacing-4);
  background-color: var(--primary);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: var(--primary-dark);
  }
`;

const SprintsList = styled.div`
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
`;

const NoSprintsMessage = styled.div`
  text-align: center;
  padding: var(--spacing-8);
  background-color: var(--neutral-0);
  border-radius: var(--border-radius-lg);
  color: var(--neutral-600);
`;

const SprintManagement = () => {
  const { projectId } = useParams();
  const [sprints, setSprints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [createModalOpen, setCreateModalOpen] = useState(false);

  useEffect(() => {
    fetchSprints();
  }, [projectId]);

  const fetchSprints = async () => {
    try {
      const response = await sprintAPI.getByProject(projectId);
      setSprints(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Error fetching sprints:', err);
      setError('Failed to load sprints');
      setLoading(false);
    }
  };

  const handleCreateSprint = async (sprintData) => {
    try {
      const response = await sprintAPI.create(sprintData, projectId);
      setSprints(prev => [...prev, response.data]);
      setCreateModalOpen(false);
    } catch (err) {
      console.error('Error creating sprint:', err);
      throw new Error('Failed to create sprint');
    }
  };

  const handleStartSprint = async (sprintId) => {
    try {
      const response = await sprintAPI.start(sprintId);
      setSprints(prev =>
        prev.map(sprint =>
          sprint.id === sprintId ? response.data : sprint
        )
      );
    } catch (err) {
      console.error('Error starting sprint:', err);
    }
  };

  const handleCompleteSprint = async (sprintId) => {
    try {
      const response = await sprintAPI.complete(sprintId);
      setSprints(prev =>
        prev.map(sprint =>
          sprint.id === sprintId ? response.data : sprint
        )
      );
    } catch (err) {
      console.error('Error completing sprint:', err);
    }
  };

  const handleUpdateTask = async (taskId, newStatus, updatedTasks) => {
    try {
      if (updatedTasks) {
        // If we received updated tasks from AddTaskToSprintModal
        fetchSprints(); // Refresh sprints to show new tasks
        return;
      }
  
      if (taskId && newStatus) {
        // Handle drag-and-drop status updates
        await taskAPI.updateStatus(taskId, newStatus);
        fetchSprints();
      }
    } catch (error) {
      console.error('Error updating task:', error);
    }
  };

  if (loading) {
    return (
      <PageContainer>
        <div>Loading sprints...</div>
      </PageContainer>
    );
  }

  if (error) {
    return (
      <PageContainer>
        <div style={{ color: 'var(--error)' }}>{error}</div>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <PageHeader>
        <PageTitle>Sprint Management</PageTitle>
        <Button onClick={() => setCreateModalOpen(true)}>
          Create Sprint
        </Button>
      </PageHeader>

      <SprintsList>
        {sprints.length > 0 ? (
          sprints.map(sprint => (
            <SprintBoard
              key={sprint.id}
              sprint={sprint}
              onStartSprint={handleStartSprint}
              onCompleteSprint={handleCompleteSprint}
              onUpdateTask={handleUpdateTask}
            />
          ))
        ) : (
          <NoSprintsMessage>
            <h3>No sprints found</h3>
            <p>Create your first sprint to start planning your work</p>
          </NoSprintsMessage>
        )}
      </SprintsList>

      {createModalOpen && (
        <CreateSprintModal
          onClose={() => setCreateModalOpen(false)}
          onCreateSprint={handleCreateSprint}
        />
      )}
    </PageContainer>
  );
};

export default SprintManagement;