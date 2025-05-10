import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import axios from 'axios';
import { API_URL } from '../config/constants';
import { projectAPI } from '../config/api';

const PageContainer = styled.div`
  max-width: 800px;
  margin: 0 auto;
`;

const PageTitle = styled.h1`
  font-size: 24px;
  font-weight: 500;
  margin-bottom: var(--spacing-6);
  color: var(--neutral-900);
`;

const FormContainer = styled.div`
  background-color: var(--neutral-0);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-6);
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

const createProjectKey = (name) => {
  if (!name) return '';
  
  // Generate key from first letters of words, max 5 characters
  return name
    .split(' ')
    .map(word => word.charAt(0).toUpperCase())
    .join('')
    .substring(0, 5);
};

const CreateProject = () => {
  const [formData, setFormData] = useState({
    name: '',
    key: '',
    description: ''
  });
  
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    
    setFormData(prev => {
      // If name field is changing, auto-generate the key
      if (name === 'name') {
        return {
          ...prev,
          [name]: value,
          key: createProjectKey(value)
        };
      }
      
      return {
        ...prev,
        [name]: value
      };
    });
    
    // Clear error when user types
    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: ''
      });
    }
  };
  
  const validate = () => {
    const newErrors = {};
    
    if (!formData.name.trim()) {
      newErrors.name = 'Project name is required';
    }
    
    if (!formData.key.trim()) {
      newErrors.key = 'Project key is required';
    } else if (!/^[A-Z]{2,5}$/.test(formData.key)) {
      newErrors.key = 'Key must be 2-5 uppercase letters';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (validate()) {
      setIsSubmitting(true);
      
      try {
        // Use projectAPI instead of direct axios call
        const response = await projectAPI.create(formData);
        
        // No need for setTimeout, the response is real
        navigate('/dashboard', { 
          state: { 
            message: 'Project created successfully!' 
          } 
        });
      } catch (err) {
        console.error('Error creating project:', err);
        setErrors({
          submit: err.response?.data?.message || 'Failed to create project'
        });
      } finally {
        setIsSubmitting(false);
      }
    }
  };
  
  const handleCancel = () => {
    navigate('/dashboard');
  };

  return (
    <PageContainer>
      <PageTitle>Create a new project</PageTitle>
      
      <FormContainer>
        <form onSubmit={handleSubmit}>
          <FormGroup>
            <Label htmlFor="name">Project Name</Label>
            <Input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="Enter project name"
            />
            {errors.name && <ErrorMessage>{errors.name}</ErrorMessage>}
          </FormGroup>
          
          <FormGroup>
            <Label htmlFor="key">Project Key</Label>
            <Input
              type="text"
              id="key"
              name="key"
              value={formData.key}
              onChange={handleChange}
              placeholder="e.g., PRJ"
              maxLength={5}
            />
            <div style={{ fontSize: '12px', color: 'var(--neutral-600)', marginTop: 'var(--spacing-1)' }}>
              2-5 uppercase letters, used as a prefix for task IDs
            </div>
            {errors.key && <ErrorMessage>{errors.key}</ErrorMessage>}
          </FormGroup>
          
          <FormGroup>
            <Label htmlFor="description">Description</Label>
            <TextArea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              placeholder="Describe the purpose of this project"
            />
          </FormGroup>
          
          {errors.submit && <ErrorMessage>{errors.submit}</ErrorMessage>}
          
          <ButtonGroup>
            <CancelButton type="button" onClick={handleCancel}>
              Cancel
            </CancelButton>
            <CreateButton type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Creating...' : 'Create Project'}
            </CreateButton>
          </ButtonGroup>
        </form>
      </FormContainer>
    </PageContainer>
  );
};

export default CreateProject;