package com.nhom6.taskmanagement.service;

import java.time.LocalDateTime;
import java.util.List;

import com.nhom6.taskmanagement.dao.ProjectMemberDAO;
import com.nhom6.taskmanagement.model.ProjectMember;
import com.nhom6.taskmanagement.model.ProjectRole;

public class ProjectMemberService {
    private final ProjectMemberDAO projectMemberDAO;

    public ProjectMemberService() {
        this.projectMemberDAO = new ProjectMemberDAO();
    }

    public List<ProjectMember> getMembersByProject(Long projectId) {
        return projectMemberDAO.findByProjectId(projectId);
    }

    public ProjectMember getMemberById(Long id) {
        return projectMemberDAO.findById(id);
    }

    public ProjectMember addMember(Long projectId, Long userId, ProjectRole role) {
        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(userId);
        member.setRole(role);
        member.setJoinedAt(LocalDateTime.now());
        member.setCreatedBy(UserService.getCurrentUser().getId());
        member.setCreatedAt(LocalDateTime.now());
        member.setIsDeleted(false);
        
        return projectMemberDAO.save(member);
    }

    public ProjectMember updateMemberRole(Long memberId, ProjectRole newRole) {
        ProjectMember member = projectMemberDAO.findById(memberId);
        if (member != null) {
            member.setRole(newRole);
            member.setUpdatedAt(LocalDateTime.now());
            return projectMemberDAO.update(member);
        }
        return null;
    }

    public void removeMember(Long memberId) {
        ProjectMember member = projectMemberDAO.findById(memberId);
        if (member != null) {
            member.setIsDeleted(true);
            member.setDeletedAt(LocalDateTime.now());
            member.setDeletedBy(UserService.getCurrentUser().getId());
            projectMemberDAO.update(member);
        }
    }

    public boolean isProjectMember(Long projectId, Long userId) {
        List<ProjectMember> members = getMembersByProject(projectId);
        return members.stream()
            .anyMatch(m -> m.getUserId().equals(userId));
    }

    public ProjectRole getMemberRole(Long projectId, Long userId) {
        List<ProjectMember> members = getMembersByProject(projectId);
        return members.stream()
            .filter(m -> m.getUserId().equals(userId))
            .map(ProjectMember::getRole)
            .findFirst()
            .orElse(null);
    }
} 