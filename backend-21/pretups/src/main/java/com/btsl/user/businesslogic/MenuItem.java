/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;


import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Get ChannelUser model
 * 
 * @author VENKATESAN.S
 */

@Getter
@Setter
public class MenuItem implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private boolean menuItems;
    private java.lang.String menuName;
    private java.lang.String url;
    private java.lang.String parentLevel;
    private java.lang.String level;
    private java.lang.String pageCode;
    private java.lang.String moduleCode;
    private java.lang.String type;
    private String fromTimeStr;
    private String toTimeStr;
    private String roleCode;
    private String eventCode;
    private String eventName;
    private String eventLabelKey;
    private String roleLabelKey;
    private List<MenuEventsVO> listmenuEventVO;
	public boolean isMenuItems() {
		return menuItems;
	}
	public void setMenuItems(boolean menuItems) {
		this.menuItems = menuItems;
	}
	public java.lang.String getMenuName() {
		return menuName;
	}
	public void setMenuName(java.lang.String menuName) {
		this.menuName = menuName;
	}
	public java.lang.String getUrl() {
		return url;
	}
	public void setUrl(java.lang.String url) {
		this.url = url;
	}
	public java.lang.String getParentLevel() {
		return parentLevel;
	}
	public void setParentLevel(java.lang.String parentLevel) {
		this.parentLevel = parentLevel;
	}
	public java.lang.String getLevel() {
		return level;
	}
	public void setLevel(java.lang.String level) {
		this.level = level;
	}
	public java.lang.String getPageCode() {
		return pageCode;
	}
	public void setPageCode(java.lang.String pageCode) {
		this.pageCode = pageCode;
	}
	public java.lang.String getModuleCode() {
		return moduleCode;
	}
	public void setModuleCode(java.lang.String moduleCode) {
		this.moduleCode = moduleCode;
	}
	public java.lang.String getType() {
		return type;
	}
	public void setType(java.lang.String type) {
		this.type = type;
	}
	public String getFromTimeStr() {
		return fromTimeStr;
	}
	public void setFromTimeStr(String fromTimeStr) {
		this.fromTimeStr = fromTimeStr;
	}
	public String getToTimeStr() {
		return toTimeStr;
	}
	public void setToTimeStr(String toTimeStr) {
		this.toTimeStr = toTimeStr;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public String getEventCode() {
		return eventCode;
	}
	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventLabelKey() {
		return eventLabelKey;
	}
	public void setEventLabelKey(String eventLabelKey) {
		this.eventLabelKey = eventLabelKey;
	}
	public String getRoleLabelKey() {
		return roleLabelKey;
	}
	public void setRoleLabelKey(String roleLabelKey) {
		this.roleLabelKey = roleLabelKey;
	}
	public List<MenuEventsVO> getListmenuEventVO() {
		return listmenuEventVO;
	}
	public void setListmenuEventVO(List<MenuEventsVO> listmenuEventVO) {
		this.listmenuEventVO = listmenuEventVO;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
    
    
    
}
