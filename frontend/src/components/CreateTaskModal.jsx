import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { TASK_PRIORITY, TASK_TYPE, TASK_STATUS } from '../config/constants';
import { userAPI, taskAPI } from '../config/api';
import { toast } from 'react-toastify';

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
  max-height: 90vh;
  overflow-y: auto;
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
  transition: color 0.2s;
  
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

const Select = styled.select`
  width: 100%;
  padding: var(--spacing-3);
  border: 1px solid var(--neutral-300);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  background-color: white;
  
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

const FormRow = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-4);
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;


const CreateTaskModal = ({ project, onClose, onCreateTask }) => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    type: TASK_TYPE.TASK,
    priority: TASK_PRIORITY.MEDIUM,
    assigneeId: '',
    dueDate: ''
  });
  
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [teamMembers, setTeamMembers] = useState([]);
  const [loadingMembers, setLoadingMembers] = useState(true);
  
  // Fetch team members from the backend
  useEffect(() => {
    const fetchTeamMembers = async () => {
      try {
        setLoadingMembers(true);
        // You might need to adjust this API call based on your backend structure
        // For example, you might need to fetch members for this specific project
        const response = await userAPI.getAll();
        console.log('Team members fetched:', response.data.filter(member => member.role !== 'PRODUCT_OWNER'));
        setTeamMembers(response.data.filter(member => member.role !== 'PRODUCT_OWNER'));
      } catch (err) {
        console.error('Error fetching team members:', err);
        toast.error('Failed to load team members');
      } finally {
        setLoadingMembers(false);
      }
    };

    fetchTeamMembers();
  }, [project.id]);
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    
    setFormData({
      ...formData,
      [name]: value
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
    
    if (!formData.title.trim()) {
      newErrors.title = 'Task title is required';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (validate()) {
      setIsSubmitting(true);
      
      try {
        // Fetch assignee if assigneeId exists
        let assignee = null;
        if (formData.assigneeId) {
          const assigneeResponse = await userAPI.getById(formData.assigneeId);
          assignee = assigneeResponse.data;
        }

        const taskData = {
          title: formData.title,
          description: formData.description,
          type: formData.type,
          priority: formData.priority,
          status: TASK_STATUS.TODO,
          assignee: assignee, // Send the full user object as assignee
          dueDate: formData.dueDate || null
        };
        
        console.log('Sending task data:', taskData);
        console.log('Project ID:', project.id);
        
        // Create task via API
        const response = await taskAPI.create(taskData, project.id);
        console.log('Task created:', response.data);
        
        // Notify parent component
        onCreateTask(response.data);
        toast.success('Task created successfully');
        onClose();
      } catch (err) {
        console.error('Error creating task:', err);
        setErrors({
          submit: err.response?.data?.message || 'Failed to create task'
        });
      } finally {
        setIsSubmitting(false);
      }
    }
  };

  return (
    <ModalOverlay onClick={onClose}>
      <ModalContent onClick={e => e.stopPropagation()}>
        <ModalHeader>
          <ModalTitle>Create Task</ModalTitle>
          <CloseButton onClick={onClose}>×</CloseButton>
        </ModalHeader>
        
        <form onSubmit={handleSubmit}>
          <FormGroup>
            <Label htmlFor="title">Task Title</Label>
            <Input
              type="text"
              id="title"
              name="title"
              value={formData.title}
              onChange={handleChange}
              placeholder="Enter task title"
            />
            {errors.title && <ErrorMessage>{errors.title}</ErrorMessage>}
          </FormGroup>
          
          <FormGroup>
            <Label htmlFor="description">Description</Label>
            <TextArea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              placeholder="Describe the task"
            />
          </FormGroup>
          
          <FormRow>
            <FormGroup>
              <Label htmlFor="type">Task Type</Label>
              <Select
                id="type"
                name="type"
                value={formData.type}
                onChange={handleChange}
              >
                <option value={TASK_TYPE.TASK}>Task</option>
                <option value={TASK_TYPE.BUG}>Bug</option>
                <option value={TASK_TYPE.STORY}>Story</option>
                <option value={TASK_TYPE.EPIC}>Epic</option>
              </Select>
            </FormGroup>
            
            <FormGroup>
              <Label htmlFor="priority">Priority</Label>
              <Select
                id="priority"
                name="priority"
                value={formData.priority}
                onChange={handleChange}
              >
                <option value={TASK_PRIORITY.HIGHEST}>Highest</option>
                <option value={TASK_PRIORITY.HIGH}>High</option>
                <option value={TASK_PRIORITY.MEDIUM}>Medium</option>
                <option value={TASK_PRIORITY.LOW}>Low</option>
                <option value={TASK_PRIORITY.LOWEST}>Lowest</option>
              </Select>
            </FormGroup>
          </FormRow>
          
          <FormRow>
            <FormGroup>
              <Label htmlFor="assigneeId">Assignee</Label>
              <Select
                id="assigneeId"
                name="assigneeId"
                value={formData.assigneeId}
                onChange={handleChange}
                disabled={loadingMembers}
              >
                <option value="">Unassigned</option>
                {teamMembers.map(member => (
                  <option key={member.id} value={member.id}>
                    {member.name || member.username || member.email}
                  </option>
                ))}
              </Select>
              {loadingMembers && <div style={{ fontSize: '12px', color: 'var(--neutral-600)' }}>Loading team members...</div>}
            </FormGroup>
            
            <FormGroup>
              <Label htmlFor="dueDate">Due Date (Optional)</Label>
              <Input
                type="date"
                id="dueDate"
                name="dueDate"
                value={formData.dueDate}
                onChange={handleChange}
              />
            </FormGroup>
          </FormRow>
          
          {errors.submit && <ErrorMessage>{errors.submit}</ErrorMessage>}
          
          <ButtonGroup>
            <CancelButton type="button" onClick={onClose}>
              Cancel
            </CancelButton>
            <CreateButton type="submit" disabled={isSubmitting || loadingMembers}>
              {isSubmitting ? 'Creating...' : 'Create Task'}
            </CreateButton>
          </ButtonGroup>
        </form>
      </ModalContent>
    </ModalOverlay>
  );
};

export default CreateTaskModal;