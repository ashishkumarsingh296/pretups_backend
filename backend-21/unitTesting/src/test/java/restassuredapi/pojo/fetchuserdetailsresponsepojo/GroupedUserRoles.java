
package restassuredapi.pojo.fetchuserdetailsresponsepojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "groupRolesMap",
    "roleType",
    "roleTypeDesc",
    "systemRolesMap"
})
public class GroupedUserRoles {

    @JsonProperty("groupRolesMap")
    private GroupRolesMap groupRolesMap;
    @JsonProperty("roleType")
    private String roleType;
    @JsonProperty("roleTypeDesc")
    private String roleTypeDesc;
    @JsonProperty("systemRolesMap")
    private SystemRolesMap systemRolesMap;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("groupRolesMap")
    public GroupRolesMap getGroupRolesMap() {
        return groupRolesMap;
    }

    @JsonProperty("groupRolesMap")
    public void setGroupRolesMap(GroupRolesMap groupRolesMap) {
        this.groupRolesMap = groupRolesMap;
    }

    @JsonProperty("roleType")
    public String getRoleType() {
        return roleType;
    }

    @JsonProperty("roleType")
    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    @JsonProperty("roleTypeDesc")
    public String getRoleTypeDesc() {
        return roleTypeDesc;
    }

    @JsonProperty("roleTypeDesc")
    public void setRoleTypeDesc(String roleTypeDesc) {
        this.roleTypeDesc = roleTypeDesc;
    }

    @JsonProperty("systemRolesMap")
    public SystemRolesMap getSystemRolesMap() {
        return systemRolesMap;
    }

    @JsonProperty("systemRolesMap")
    public void setSystemRolesMap(SystemRolesMap systemRolesMap) {
        this.systemRolesMap = systemRolesMap;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
