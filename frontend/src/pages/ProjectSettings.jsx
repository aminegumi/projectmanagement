import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
// Remove these imports
// import axios from 'axios';
// import { API_URL } from '../config/constants';
// Add these imports
import { projectAPI, userAPI } from '../config/api';
import { toast } from 'react-toastify';

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

const TabsContainer = styled.div`
  display: flex;
  border-bottom: 1px solid var(--neutral-300);
  margin-bottom: var(--spacing-6);
`;

const Tab = styled.button`
  padding: var(--spacing-3) var(--spacing-4);
  background: none;
  border: none;
  border-bottom: 2px solid ${props => props.active ? 'var(--primary)' : 'transparent'};
  color: ${props => props.active ? 'var(--primary)' : 'var(--neutral-700)'};
  font-weight: ${props => props.active ? '500' : '400'};
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    color: var(--primary);
  }
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

const SaveButton = styled(Button)`
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

const CancelButton = styled(Button)`
  background-color: var(--neutral-0);
  color: var(--neutral-700);
  border: 1px solid var(--neutral-300);
  
  &:hover {
    background-color: var(--neutral-100);
  }
`;

const DeleteButton = styled(Button)`
  background-color: var(--error);
  color: white;
  border: none;
  
  &:hover {
    background-color: #e03e2d;
  }
`;

const MembersTable = styled.table`
  width: 100%;
  border-collapse: collapse;
`;

const TableHeader = styled.th`
  text-align: left;
  padding: var(--spacing-3);
  border-bottom: 1px solid var(--neutral-300);
  color: var(--neutral-700);
  font-weight: 500;
`;

const TableCell = styled.td`
  padding: var(--spacing-3);
  border-bottom: 1px solid var(--neutral-200);
`;

const UserRow = styled.tr`
  &:hover {
    background-color: var(--neutral-100);
  }
`;

const UserAvatar = styled.div`
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: var(--primary-lightest);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
`;

const UserInfo = styled.div`
  display: flex;
  align-items: center;
`;

const UserDetails = styled.div`
  margin-left: var(--spacing-3);
`;

const UserName = styled.div`
  font-weight: 500;
`;

const UserEmail = styled.div`
  font-size: 12px;
  color: var(--neutral-600);
`;

const Select = styled.select`
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

const AddMemberForm = styled.form`
  display: flex;
  margin-bottom: var(--spacing-4);
  
  @media (max-width: 768px) {
    flex-direction: column;
  }
`;

const MemberInput = styled(Input)`
  flex: 1;
  margin-right: var(--spacing-3);
  
  @media (max-width: 768px) {
    margin-right: 0;
    margin-bottom: var(--spacing-3);
  }
`;

const AddButton = styled(Button)`
  background-color: var(--primary);
  color: white;
  border: none;
  
  @media (max-width: 768px) {
    align-self: flex-end;
  }
  
  &:hover {
    background-color: var(--primary-dark);
  }
`;

const ErrorMessage = styled.div`
  color: var(--error);
  font-size: 14px;
  margin-top: var(--spacing-2);
`;

const SuccessMessage = styled.div`
  color: var(--success);
  font-size: 14px;
  margin-top: var(--spacing-2);
`;

const DangerZone = styled.div`
  margin-top: var(--spacing-8);
  padding: var(--spacing-4);
  border: 1px solid var(--error-light);
  border-radius: var(--border-radius-md);
  background-color: var(--error-light);
`;

const DangerTitle = styled.h3`
  color: var(--error);
  font-size: 16px;
  font-weight: 500;
  margin-bottom: var(--spacing-3);
`;

const DangerDescription = styled.p`
  color: var(--neutral-800);
  margin-bottom: var(--spacing-4);
`;



// Keep all your styled components...

const ProjectSettings = () => {
  const { projectId } = useParams();
  const navigate = useNavigate();
  
  const [activeTab, setActiveTab] = useState('details');
  const [project, setProject] = useState(null);
  const [members, setMembers] = useState([]);
  const [newMemberEmail, setNewMemberEmail] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    key: '',
    description: ''
  });
  
  // User roles for select options
  const userRoles = [
    { id: 'MEMBER', name: 'Member' },
    { id: 'PRODUCT_OWNER', name: 'Product Owner' },
    { id: 'SCRUM_MASTER', name: 'Scrum Master' }
  ];
  
  const [availableUsers, setAvailableUsers] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState('');
  
  // Update your useEffect to fetch all users as well
  useEffect(() => {
    const fetchProjectData = async () => {
      try {
        setLoading(true);
        
        // Fetch project details
        const projectResponse = await projectAPI.getById(projectId);
        const projectData = projectResponse.data;
        
        // Set project data
        setProject(projectData);
        setFormData({
          name: projectData.name,
          key: projectData.key,
          description: projectData.description || ''
        });
        
        // Fetch project members
        try {
          const membersResponse = await projectAPI.getMembers(projectId);
          setMembers(membersResponse.data);
        } catch (memberErr) {
          console.error('Could not fetch members:', memberErr);
          if (projectData.lead) {
            setMembers([{
              id: projectData.lead.id,
              name: projectData.lead.name,
              email: projectData.lead.email,
              role: 'PRODUCT_OWNER'
            }]);
          }
        }
        
        // Fetch all available users
        try {
          const usersResponse = await userAPI.getAll();
          setAvailableUsers(usersResponse.data);
        } catch (usersErr) {
          console.error('Could not fetch users:', usersErr);
        }
        
        setLoading(false);
      } catch (err) {
        console.error('Error fetching project data:', err);
        setError('Failed to load project data: ' + (err.response?.data?.message || err.message));
        setLoading(false);
      }
    };
    
    fetchProjectData();
  }, [projectId]);
  
  // Keep handlers but update them to use real API calls
  
  const handleSaveDetails = async (e) => {
    e.preventDefault();
    
    try {
      // Call the API to update project details
      const updatedProject = await projectAPI.update(projectId, formData);
      
      // Update local state with response data
      setProject(updatedProject.data);
      
      toast.success('Project details updated successfully');
    } catch (err) {
      console.error('Error updating project:', err);
      setError('Failed to update project details: ' + (err.response?.data?.message || err.message));
    }
  };
  
  const handleAddMember = async (e) => {
    e.preventDefault();
    
    if (!selectedUserId) {
      setError('Please select a user to add');
      return;
    }
    
    try {
      setError(null);
      
      // Find the selected user in the available users list
      const selectedUser = availableUsers.find(user => user.id.toString() === selectedUserId);
      
      if (!selectedUser) {
        setError('Selected user not found');
        return;
      }
      
      // Check if member already exists
      if (members.some(member => member.id.toString() === selectedUserId)) {
        setError('This user is already a member of the project');
        return;
      }
      
      // Add member to project
      const response = await projectAPI.addMember(projectId, selectedUserId);
      
      // Update the members list
      setMembers([...members, {
        id: selectedUser.id,
        name: selectedUser.name,
        email: selectedUser.email,
        role: 'MEMBER' // Default role
      }]);
      
      // Reset selection
      setSelectedUserId('');
      toast.success('Member added successfully');
    } catch (err) {
      console.error('Error adding member:', err);
      setError('Failed to add member: ' + (err.response?.data?.message || err.message));
    }
  };
  
  const handleRoleChange = async (userId, newRole) => {
    try {
      // Call API to update user role
      await projectAPI.updateMemberRole(projectId, userId, newRole);
      
      // Update local state
      const updatedMembers = members.map(member => 
        member.id === userId ? { ...member, role: newRole } : member
      );
      
      setMembers(updatedMembers);
      toast.success('Member role updated successfully');
    } catch (err) {
      console.error('Error updating role:', err);
      setError('Failed to update role: ' + (err.response?.data?.message || err.message));
    }
  };
  
  const handleRemoveMember = async (userId) => {
    try {
      // Call API to remove member
      await projectAPI.removeMember(projectId, userId);
      
      // Update local state
      const updatedMembers = members.filter(member => member.id !== userId);
      setMembers(updatedMembers);
      
      toast.success('Member removed successfully');
    } catch (err) {
      console.error('Error removing member:', err);
      setError('Failed to remove member: ' + (err.response?.data?.message || err.message));
    }
  };
  
  const handleDeleteProject = async () => {
    const confirmed = window.confirm(
      'Are you sure you want to delete this project? This action cannot be undone!'
    );
    
    if (confirmed) {
      try {
        // Call API to delete project
        await projectAPI.delete(projectId);
        
        navigate('/dashboard', { 
          state: { 
            message: 'Project deleted successfully' 
          } 
        });
      } catch (err) {
        console.error('Error deleting project:', err);
        setError('Failed to delete project: ' + (err.response?.data?.message || err.message));
      }
    }
  };

  const handleChange = (e) => {
  const { name, value } = e.target;
  setFormData({
    ...formData,
    [name]: value
  });
};

// Add the missing handleTabChange function
const handleTabChange = (tab) => {
  setActiveTab(tab);
};
  
  const getInitials = (name) => {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  };
  
  if (loading) {
    return (
      <div style={{ padding: 'var(--spacing-10)', textAlign: 'center' }}>
        <p>Loading project settings...</p>
      </div>
    );
  }
  
  if (error && !project) {
    return (
      <div style={{ padding: 'var(--spacing-10)', textAlign: 'center', color: 'var(--error)' }}>
        <p>{error}</p>
        <Button onClick={() => navigate('/dashboard')}>Back to Dashboard</Button>
      </div>
    );
  }

  return (
    <PageContainer>
      <PageTitle>Project Settings</PageTitle>
      
      <TabsContainer>
        <Tab 
          active={activeTab === 'details'} 
          onClick={() => handleTabChange('details')}
        >
          Details
        </Tab>
        <Tab 
          active={activeTab === 'members'} 
          onClick={() => handleTabChange('members')}
        >
          Members
        </Tab>
      </TabsContainer>
      
      {activeTab === 'details' && (
        <FormContainer>
          <form onSubmit={handleSaveDetails}>
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
            
            {error && <ErrorMessage>{error}</ErrorMessage>}
            {success && <SuccessMessage>{success}</SuccessMessage>}
            
            <ButtonGroup>
              <CancelButton type="button" onClick={() => navigate(`/projects/${projectId}`)}>
                Cancel
              </CancelButton>
              <SaveButton type="submit">
                Save Changes
              </SaveButton>
            </ButtonGroup>
          </form>
          
          <DangerZone>
            <DangerTitle>Danger Zone</DangerTitle>
            <DangerDescription>
              Once you delete a project, there is no going back. Please be certain.
            </DangerDescription>
            <DeleteButton onClick={handleDeleteProject}>
              Delete Project
            </DeleteButton>
          </DangerZone>
        </FormContainer>
      )}
      
      {activeTab === 'members' && (
        <FormContainer>
          <h2 style={{ marginBottom: 'var(--spacing-4)' }}>Project Members</h2>
          
          <AddMemberForm onSubmit={handleAddMember}>
    <Select
      value={selectedUserId}
      onChange={e => setSelectedUserId(e.target.value)}
      style={{ flex: 1, marginRight: 'var(--spacing-3)' }}
    >
      <option value="">Select a user to add</option>
      {availableUsers
        // Filter out users that are already members
        .filter(user => !members.some(member => member.id === user.id))
        .map(user => (
          <option key={user.id} value={user.id}>
            {user.name || user.email}
          </option>
        ))}
    </Select>
    <AddButton type="submit">
      Add Member
    </AddButton>
  </AddMemberForm>
  
  {error && <ErrorMessage>{error}</ErrorMessage>}
  {success && <SuccessMessage>{success}</SuccessMessage>}
          
          <MembersTable>
            <thead>
              <tr>
                <TableHeader>User</TableHeader>
                <TableHeader>Role</TableHeader>
                <TableHeader>Actions</TableHeader>
              </tr>
            </thead>
            <tbody>
              {members.map(member => (
                <UserRow key={member.id}>
                  <TableCell>
                    <UserInfo>
                      <UserAvatar>{getInitials(member.name)}</UserAvatar>
                      <UserDetails>
                        <UserName>{member.name}</UserName>
                        <UserEmail>{member.email}</UserEmail>
                      </UserDetails>
                    </UserInfo>
                  </TableCell>
                  <TableCell>
                    <Select
                      value={member.role}
                      onChange={e => handleRoleChange(member.id, e.target.value)}
                    >
                      {userRoles.map(role => (
                        <option key={role.id} value={role.id}>
                          {role.name}
                        </option>
                      ))}
                    </Select>
                  </TableCell>
                  <TableCell>
                    <Button onClick={() => handleRemoveMember(member.id)}>
                      Remove
                    </Button>
                  </TableCell>
                </UserRow>
              ))}
              
              {members.length === 0 && (
                <tr>
                  <TableCell colSpan="3" style={{ textAlign: 'center' }}>
                    No members found
                  </TableCell>
                </tr>
              )}
            </tbody>
          </MembersTable>
        </FormContainer>
      )}
    </PageContainer>
  );
};

export default ProjectSettings;