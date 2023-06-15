package top.tonydon.syncplayer.entity;

import java.time.LocalDateTime;


public class ProjectVersion {
    private Long id;
    private Long projectId;
    private String projectName;
    private String version;
    private Integer versionNumber;
    private String description;
    private Boolean isForced;       // 是否强制更新，默认false
    private Boolean isBeta;         // 是否是beta版本，默认false
    private Boolean isCompatible;   // 否兼容旧版本，默认true

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsForced() {
        return isForced;
    }

    public ProjectVersion setIsForced(Boolean forced) {
        isForced = forced;
        return this;
    }

    public Boolean getIsBeta() {
        return isBeta;
    }

    public ProjectVersion setIsBeta(Boolean beta) {
        isBeta = beta;
        return this;
    }

    public Boolean getIsCompatible() {
        return isCompatible;
    }

    public ProjectVersion setIsCompatible(Boolean compatible) {
        isCompatible = compatible;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ProjectVersion{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", version='" + version + '\'' +
                ", versionNumber=" + versionNumber +
                ", description='" + description + '\'' +
                ", isForced=" + isForced +
                ", isBeta=" + isBeta +
                ", isCompatible=" + isCompatible +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}

