import React, { useState } from 'react';
import styled from 'styled-components';

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
  max-width: 500px;
  padding: var(--spacing-6);
`;

const ModalHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
`;

const ModalTitle = styled.h2`
  font-size: 20px;
  font-weight: 500;
  color: var(--neutral-900);
`;

const CloseButton = styled.button`
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: var(--neutral-600);
  
  &:hover {
    color: var(--neutral-900);
  }
`;

const FormGroup = styled.div`
  margin-bottom: var(--spacing-4);
`;

const Label = styled.label`
  display: block;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: var(--spacing-2);
  color: var(--neutral-800);
`;

const Input = styled.input`
  width: 100%;
  padding: var(--spacing-3);
  border: 1px solid var(--neutral-300);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  
  &:focus {
    outline: none;
    border-color: var(--primary-light);
    box-shadow: 0 0 0 2px var(--primary-lightest);
  }
`;

const TextArea = styled.textarea`
  width: 100%;
  padding: var(--spacing-3);
  border: 1px solid var(--neutral-300);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  min-height: 100px;
  resize: vertical;
  
  &:focus {
    outline: none;
    border-color: var(--primary-light);
    box-shadow: 0 0 0 2px var(--primary-lightest);
  }
`;

const ButtonGroup = styled.div`
  display: flex;
  justify-content: flex-end;
  margin-top: var(--spacing-6);
`;

const Button = styled.button`
  padding: var(--spacing-2) var(--spacing-6);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  
  &:not(:last-child) {
    margin-right: var(--spacing-3);
  }
`;

const CancelButton = styled(Button)`
  background-color: var(--neutral-0);
  color: var(--neutral-700);
  border: 1px solid var(--neutral-300);
  
  &:hover {
    background-color: var(--neutral-100);
  }
`;

const CreateButton = styled(Button)`
  background-color: var(--primary);
  color: white;
  border: none;
  
  &:hover {
    background-color: var(--primary-dark);
  }
  
  &:disabled {
    background-color: var(--neutral-400);
    cursor: not-allowed;
  }
`;

const ErrorMessage = styled.div`
  color: var(--error);
  font-size: 14px;
  margin-top: var(--spacing-2);
`;

const CreateSprintModal = ({ onClose, onCreateSprint }) => {
  const [formData, setFormData] = useState({
    name: '',
    goal: '',
    startDate: '',
    endDate: ''
  });
  
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    if (error) setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate form
    if (!formData.name.trim()) {
      setError('Sprint name is required');
      return;
    }
    
    if (!formData.startDate) {
      setError('Start date is required');
      return;
    }
    
    if (!formData.endDate) {
      setError('End date is required');
      return;
    }
    
    if (new Date(formData.endDate) <= new Date(formData.startDate)) {
      setError('End date must be after start date');
      return;
    }

    setIsSubmitting(true);
    
    try {
      await onCreateSprint(formData);
      onClose();
    } catch (err) {
      setError(err.message || 'Failed to create sprint');
      setIsSubmitting(false);
    }
  };

  return (
    <ModalOverlay onClick={onClose}>
      <ModalContent onClick={e => e.stopPropagation()}>
        <ModalHeader>
          <ModalTitle>Create New Sprint</ModalTitle>
          <CloseButton onClick={onClose}>×</CloseButton>
        </ModalHeader>

        <form onSubmit={handleSubmit}>
          <FormGroup>
            <Label htmlFor="name">Sprint Name</Label>
            <Input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="Enter sprint name"
            />
          </FormGroup>

          <FormGroup>
            <Label htmlFor="goal">Sprint Goal</Label>
            <TextArea
              id="goal"
              name="goal"
              value={formData.goal}
              onChange={handleChange}
              placeholder="What do you want to achieve in this sprint?"
            />
          </FormGroup>

          <FormGroup>
            <Label htmlFor="startDate">Start Date</Label>
            <Input
              type="date"
              id="startDate"
              name="startDate"
              value={formData.startDate}
              onChange={handleChange}
            />
          </FormGroup>

          <FormGroup>
            <Label htmlFor="endDate">End Date</Label>
            <Input
              type="date"
              id="endDate"
              name="endDate"
              value={formData.endDate}
              onChange={handleChange}
            />
          </FormGroup>

          {error && <ErrorMessage>{error}</ErrorMessage>}

          <ButtonGroup>
            <CancelButton type="button" onClick={onClose}>
              Cancel
            </CancelButton>
            <CreateButton type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Creating...' : 'Create Sprint'}
            </CreateButton>
          </ButtonGroup>
        </form>
      </ModalContent>
    </ModalOverlay>
  );
};

export default CreateSprintModal;