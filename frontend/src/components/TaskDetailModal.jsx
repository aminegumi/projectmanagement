import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { TASK_PRIORITY, TASK_TYPE, TASK_STATUS } from '../config/constants';
import { taskAPI, userAPI, commentAPI } from '../config/api';
import { toast } from 'react-toastify';
import { authAPI } from '../config/api';

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
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
`;

const ModalHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: var(--spacing-6) var(--spacing-6) var(--spacing-4);
  border-bottom: 1px solid var(--neutral-200);
`;

const TaskInfo = styled.div`
  flex: 1;
`;

const TaskType = styled.div`
  display: flex;
  align-items: center;
  font-size: 14px;
  color: var(--neutral-700);
  margin-bottom: var(--spacing-2);
`;

const TaskTypeIcon = styled.span`
  margin-right: var(--spacing-2);
  font-size: 16px;
  color: ${props => {
    switch(props.type) {
      case TASK_TYPE.BUG: return 'var(--error)';
      case TASK_TYPE.STORY: return 'var(--success)';
      case TASK_TYPE.EPIC: return 'var(--purple)';
      default: return 'var(--primary)';
    }
  }};
`;

const TaskTitle = styled.div`
  font-size: 20px;
  font-weight: 500;
  color: var(--neutral-900);
  margin-bottom: var(--spacing-1);
`;

const TaskId = styled.div`
  font-size: 14px;
  color: var(--neutral-600);
  font-family: monospace;
`;

const CloseButton = styled.button`
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: var(--neutral-600);
  transition: color 0.2s;
  margin-left: var(--spacing-4);
  
  &:hover {
    color: var(--neutral-900);
  }
`;

const ModalBody = styled.div`
  display: grid;
  grid-template-columns: 2fr 1fr;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

const MainContent = styled.div`
  padding: var(--spacing-6);
  border-right: 1px solid var(--neutral-200);
  
  @media (max-width: 768px) {
    border-right: none;
    border-bottom: 1px solid var(--neutral-200);
  }
`;

const Sidebar = styled.div`
  padding: var(--spacing-6);
`;

const SectionTitle = styled.h3`
  font-size: 16px;
  font-weight: 500;
  margin-bottom: var(--spacing-4);
  color: var(--neutral-800);
`;

const DescriptionSection = styled.div`
  margin-bottom: var(--spacing-6);
`;

const DescriptionContent = styled.div`
  color: var(--neutral-800);
  line-height: 1.6;
  white-space: pre-wrap;
  
  p {
    margin-bottom: var(--spacing-3);
  }
`;

const CommentsSection = styled.div`
  margin-top: var(--spacing-6);
`;

const CommentsList = styled.div`
  margin-top: var(--spacing-4);
`;

const CommentItem = styled.div`
  display: flex;
  margin-bottom: var(--spacing-4);
`;

const CommentAvatar = styled.div`
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: var(--primary-lightest);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  margin-right: var(--spacing-3);
  flex-shrink: 0;
`;

const CommentContent = styled.div`
  flex: 1;
`;

const CommentHeader = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: var(--spacing-2);
`;

const CommentAuthor = styled.div`
  font-weight: 500;
  margin-right: var(--spacing-2);
`;

const CommentTime = styled.div`
  font-size: 12px;
  color: var(--neutral-500);
`;

const CommentText = styled.div`
  color: var(--neutral-800);
  line-height: 1.5;
`;

const CommentForm = styled.form`
  margin-top: var(--spacing-4);
`;

const CommentTextarea = styled.textarea`
  width: 100%;
  padding: var(--spacing-3);
  border: 1px solid var(--neutral-300);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  min-height: 80px;
  resize: vertical;
  margin-bottom: var(--spacing-3);
  
  &:focus {
    outline: none;
    border-color: var(--primary-light);
    box-shadow: 0 0 0 2px var(--primary-lightest);
  }
`;

const SidebarSection = styled.div`
  margin-bottom: var(--spacing-6);
`;

const SidebarItem = styled.div`
  margin-bottom: var(--spacing-4);
`;

const SidebarLabel = styled.div`
  font-size: 12px;
  color: var(--neutral-600);
  margin-bottom: var(--spacing-1);
`;

const SidebarValue = styled.div`
  font-size: 14px;
  color: var(--neutral-900);
  display: flex;
  align-items: center;
`;

const Select = styled.select`
  width: 100%;
  padding: var(--spacing-2);
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


// Add this with your other styled components
const DeleteButton = styled(Button)`
  background-color: var(--error);
  color: white;
  border: none;
  width: 100%;
  margin-top: var(--spacing-4);
  
  &:hover {
    background-color: var(--error-dark);
  }
`;

const UserAvatar = styled.div`
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: var(--primary-lightest);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  margin-right: var(--spacing-2);
  font-size: 12px;
`;

const PriorityTag = styled.span`
  display: inline-flex;
  align-items: center;
  color: ${props => {
    switch(props.priority) {
      case TASK_PRIORITY.HIGHEST: return 'var(--error)';
      case TASK_PRIORITY.HIGH: return 'var(--warning)';
      case TASK_PRIORITY.MEDIUM: return 'var(--primary)';
      case TASK_PRIORITY.LOW: return 'var(--success)';
      case TASK_PRIORITY.LOWEST: return 'var(--neutral-600)';
      default: return 'var(--primary)';
    }
  }};
`;

// Add these styled components
const LoadingText = styled.div`
  color: var(--neutral-600);
  padding: var(--spacing-4);
  text-align: center;
  font-style: italic;
`;

const CommentActions = styled.div`
  margin-left: auto;
  display: flex;
  gap: var(--spacing-2);
`;

const StatusTag = styled.span`
  display: inline-flex;
  align-items: center;
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--border-radius-sm);
  font-size: 12px;
  font-weight: 500;
  background-color: ${props => {
    switch(props.status) {
      case TASK_STATUS.TODO: return 'var(--neutral-200)';
      case TASK_STATUS.IN_PROGRESS: return 'var(--primary-lightest)';
      case TASK_STATUS.IN_REVIEW: return 'var(--warning-light)';
      case TASK_STATUS.DONE: return 'var(--success-light)';
      default: return 'var(--neutral-200)';
    }
  }};
  color: ${props => {
    switch(props.status) {
      case TASK_STATUS.TODO: return 'var(--neutral-700)';
      case TASK_STATUS.IN_PROGRESS: return 'var(--primary)';
      case TASK_STATUS.IN_REVIEW: return 'var(--warning)';
      case TASK_STATUS.DONE: return 'var(--success)';
      default: return 'var(--neutral-700)';
    }
  }};
`;

const getTaskTypeIcon = (type) => {
  switch(type) {
    case TASK_TYPE.BUG: return '🐞';
    case TASK_TYPE.STORY: return '📖';
    case TASK_TYPE.EPIC: return '🌟';
    default: return '📋';
  }
};

const getPriorityText = (priority) => {
  switch(priority) {
    case TASK_PRIORITY.HIGHEST: return 'Highest';
    case TASK_PRIORITY.HIGH: return 'High';
    case TASK_PRIORITY.MEDIUM: return 'Medium';
    case TASK_PRIORITY.LOW: return 'Low';
    case TASK_PRIORITY.LOWEST: return 'Lowest';
    default: return 'Medium';
  }
};

const getStatusText = (status) => {
  switch(status) {
    case TASK_STATUS.TODO: return 'To Do';
    case TASK_STATUS.IN_PROGRESS: return 'In Progress';
    case TASK_STATUS.IN_REVIEW: return 'In Review';
    case TASK_STATUS.DONE: return 'Done';
    default: return 'To Do';
  }
};

const TaskDetailModal = ({ task, project, onClose, onUpdateTask, onTaskDelete }) => {
  const [updatedTask, setUpdatedTask] = useState({ ...task });
  const [newComment, setNewComment] = useState('');
  const [teamMembers, setTeamMembers] = useState([]);
  const [loadingMembers, setLoadingMembers] = useState(true);
  const [comments, setComments] = useState([]);
  const [loadingComments, setLoadingComments] = useState(true);
  const [currentUser, setCurrentUser] = useState(null);

  useEffect(() => {
    const fetchCurrentUser = async () => {
      try {
        const response = await authAPI.getCurrentUser();
        setCurrentUser(response.data);
      } catch (error) {
        console.error('Error fetching current user:', error);
        toast.error('Failed to load user details');
      }
    };
  
    fetchCurrentUser();
  }, []);

  useEffect(() => {
    const fetchComments = async () => {
      try {
        setLoadingComments(true);
        const response = await commentAPI.getByTask(task.id);
        setComments(response.data);
      } catch (error) {
        console.error('Error fetching comments:', error);
        toast.error('Failed to load comments');
      } finally {
        setLoadingComments(false);
      }
    };

    fetchComments();
  }, [task.id]);

  useEffect(() => {
    const fetchTeamMembers = async () => {
      try {
        setLoadingMembers(true);
        const response = await userAPI.getAll();
        setTeamMembers(response.data.filter(member => member.role !== 'PRODUCT_OWNER'));
      } catch (error) {
        console.error('Error fetching team members:', error);
        toast.error('Failed to load team members');
      } finally {
        setLoadingMembers(false);
      }
    };
  
    fetchTeamMembers();
  }, []);

  
  const getInitials = (name) => {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  };
  
  const formatDate = (dateString) => {
    if (!dateString) return 'Not set';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };
  
  const formatDateTime = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };
  
  const handleStatusChange = async (e) => {
    const newStatus = e.target.value;
    try {
        console.log('Updating status to:', newStatus); // Debug log
        const response = await taskAPI.updateStatus(updatedTask.id, newStatus);
        console.log('Status update response:', response); // Debug log
        
        setUpdatedTask({ ...updatedTask, status: newStatus });
        onUpdateTask({ ...updatedTask, status: newStatus });
        toast.success('Status updated successfully');
    } catch (error) {
        console.error('Error details:', {
            message: error.message,
            response: error.response,
            status: error.response?.status
        });
        toast.error('Failed to update status');
    }
};
  
const handleAssigneeChange = async (e) => {
  const assigneeId = e.target.value ? parseInt(e.target.value) : null;
  try {
      console.log('Starting assignee update:', {
          taskId: updatedTask.id,
          assigneeId: assigneeId
      });

      // Get current task first
      const taskResponse = await taskAPI.getById(updatedTask.id);
      const currentTask = taskResponse.data;

      // If assigneeId exists, get the user data
      let assignee = null;
      if (assigneeId) {
          const userResponse = await userAPI.getById(assigneeId);
          assignee = userResponse.data;
      }

      // Prepare the task update with new assignee
      const taskToUpdate = {
          ...currentTask,
          assignee: assignee // This will be null if unassigning
      };

      // Use the general update method
      const response = await taskAPI.update(updatedTask.id, taskToUpdate);
      
      if (response.data) {
          setUpdatedTask(response.data);
          onUpdateTask(response.data);
          toast.success(assigneeId ? 'Task assigned successfully' : 'Task unassigned');
      }
  } catch (error) {
      console.error('Error updating assignee:', {
          message: error.message,
          status: error.response?.status,
          data: error.response?.data
      });
      
      if (error.response?.status === 401) {
          toast.error('Session expired. Please login again.');
      } else {
          toast.error('Failed to update assignee');
          // Revert to previous state if update failed
          setUpdatedTask({ ...updatedTask });
      }
  }
};
  
const handlePriorityChange = async (e) => {
  const newPriority = e.target.value;
  try {
      console.log('Updating task priority:', {
          taskId: updatedTask.id,
          priority: newPriority
      });

      // Get the current task from the state
      const taskToUpdate = {
          ...updatedTask,
          priority: newPriority
      };

      // Use the existing update method
      const response = await taskAPI.update(updatedTask.id, taskToUpdate);
      
      if (response.data) {
          setUpdatedTask(response.data);
          onUpdateTask(response.data);
          toast.success('Priority updated successfully');
      }
  } catch (error) {
      console.error('Error updating priority:', {
          message: error.message,
          status: error.response?.status,
          data: error.response?.data
      });
      
      if (error.response?.status === 401) {
          toast.error('Session expired. Please login again.');
      } else {
          toast.error('Failed to update priority');
          // Revert to previous state if update failed
          setUpdatedTask({ ...updatedTask });
      }
  }
};
  
const handleAddComment = async (e) => {
  e.preventDefault();
  
  if (!newComment.trim()) return;
  
  try {
    const commentData = {
      text: newComment,
      taskId: task.id
    };
    
    const response = await commentAPI.create(commentData, task.id);
    setComments(prevComments => [...prevComments, response.data]);
    setNewComment('');
    toast.success('Comment added successfully');
  } catch (error) {
    console.error('Error adding comment:', error);
    toast.error('Failed to add comment');
  }
};

const handleDeleteComment = async (commentId) => {
  if (!window.confirm('Are you sure you want to delete this comment?')) {
    return;
  }

  try {
    await commentAPI.delete(commentId);
    setComments(prevComments => prevComments.filter(c => c.id !== commentId));
    toast.success('Comment deleted successfully');
  } catch (error) {
    console.error('Error deleting comment:', error);
    toast.error('Failed to delete comment');
  }
};


const handleDeleteTask = async () => {
  if (!window.confirm('Are you sure you want to delete this task? This action cannot be undone.')) {
    return;
  }

  try {
    await taskAPI.delete(task.id);
    onTaskDelete(task.id); // Call the parent's handler
    onClose(); // Close the modal
  } catch (error) {
    console.error('Error deleting task:', error);
    toast.error('Failed to delete task');
  }
};

  return (
    <ModalOverlay onClick={onClose}>
      <ModalContent onClick={e => e.stopPropagation()}>
        <ModalHeader>
          <TaskInfo>
            <TaskType>
              <TaskTypeIcon type={updatedTask.type}>
                {getTaskTypeIcon(updatedTask.type)}
              </TaskTypeIcon>
              {updatedTask.type}
            </TaskType>
            <TaskTitle>{updatedTask.title}</TaskTitle>
            <TaskId>{updatedTask.id}</TaskId>
          </TaskInfo>
          <CloseButton onClick={onClose}>×</CloseButton>
        </ModalHeader>
        
        <ModalBody>
          <MainContent>
            <DescriptionSection>
              <SectionTitle>Description</SectionTitle>
              <DescriptionContent>
                {updatedTask.description || 'No description provided.'}
              </DescriptionContent>
            </DescriptionSection>
            
            <CommentsSection>
              <SectionTitle>Comments</SectionTitle>
              
              <CommentsList>
                {loadingComments ? (
                  <div style={{ color: 'var(--neutral-600)' }}>
                    Loading comments...
                  </div>
                ) : comments.length === 0 ? (
                  <div style={{ color: 'var(--neutral-600)' }}>
                    No comments yet.
                  </div>
                ) : (
                  comments.map(comment => (
                    <CommentItem key={comment.id}>
                      <CommentAvatar>
                        {getInitials(comment.authorName)}
                      </CommentAvatar>
                      <CommentContent>
                        <CommentHeader>
                          <CommentAuthor>{comment.authorName}</CommentAuthor>
                          <CommentTime>{formatDateTime(comment.createdAt)}</CommentTime>
                          {currentUser && comment.authorEmail === currentUser.email && (
                            <Button
                              onClick={() => handleDeleteComment(comment.id)}
                              style={{ marginLeft: 'auto', padding: '4px 8px' }}
                            >
                              Delete
                            </Button>
                          )}
                        </CommentHeader>
                        <CommentText>{comment.text}</CommentText>
                      </CommentContent>
                    </CommentItem>
                  ))
                )}
              </CommentsList>
              
              <CommentForm onSubmit={handleAddComment}>
                <CommentTextarea
                  placeholder="Add a comment..."
                  value={newComment}
                  onChange={e => setNewComment(e.target.value)}
                />
                <Button 
                  primary 
                  type="submit" 
                  disabled={!newComment.trim() || loadingComments}
                >
                  Comment
                </Button>
              </CommentForm>
            </CommentsSection>
          </MainContent>
          
          <Sidebar>
            <SidebarSection>
              <SidebarItem>
                <SidebarLabel>Status</SidebarLabel>
                <Select
                  value={updatedTask.status}
                  onChange={handleStatusChange}
                >
                  {Object.values(TASK_STATUS).map(status => (
                    <option key={status} value={status}>
                      {getStatusText(status)}
                    </option>
                  ))}
                </Select>
              </SidebarItem>
              
              <SidebarItem>
                <SidebarLabel>Assignee</SidebarLabel>
                <Select
                  value={updatedTask.assignee?.id || ''}
                  onChange={handleAssigneeChange}
                  disabled={loadingMembers}
                >
                  <option value="">Unassigned</option>
                  {teamMembers.map(member => (
                    <option key={member.id} value={member.id}>
                      {member.name || member.email}
                    </option>
                  ))}
                </Select>
                {loadingMembers && (
                  <div style={{ fontSize: '12px', color: 'var(--neutral-600)' }}>
                    Loading team members...
                  </div>
                )}
              </SidebarItem>
              
              <SidebarItem>
                <SidebarLabel>Reporter</SidebarLabel>
                <SidebarValue>
                  <UserAvatar>{getInitials(updatedTask.reporter.name)}</UserAvatar>
                  {updatedTask.reporter.name}
                </SidebarValue>
              </SidebarItem>
              
              <SidebarItem>
                <SidebarLabel>Priority</SidebarLabel>
                <Select
                  value={updatedTask.priority}
                  onChange={handlePriorityChange}
                >
                  {Object.values(TASK_PRIORITY).map(priority => (
                    <option key={priority} value={priority}>
                      {getPriorityText(priority)}
                    </option>
                  ))}
                </Select>
              </SidebarItem>
            </SidebarSection>
            
            <SidebarSection>
              <SidebarItem>
                <SidebarLabel>Created</SidebarLabel>
                <SidebarValue>
                  {formatDateTime(updatedTask.createdAt)}
                </SidebarValue>
              </SidebarItem>
              
              <SidebarItem>
                <SidebarLabel>Due Date</SidebarLabel>
                <SidebarValue>
                  {updatedTask.dueDate ? formatDate(updatedTask.dueDate) : 'Not set'}
                </SidebarValue>
              </SidebarItem>
            </SidebarSection>

            <SidebarSection>
              <SidebarItem>
                <DeleteButton
                  onClick={handleDeleteTask}
                  style={{ 
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: 'var(--spacing-2)'
                  }}
                >
                  <span>🗑️</span>
                  Delete Task
                </DeleteButton>
              </SidebarItem>
            </SidebarSection>
          </Sidebar>
        </ModalBody>
      </ModalContent>
    </ModalOverlay>
  );
};

export default TaskDetailModal;