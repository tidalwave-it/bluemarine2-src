/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2017 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * $Id$
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.importing.impl.adobe.lightroom6.jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id:$
 *
 **********************************************************************************************************************/
@Entity
@Table(name = "AgDNGProxyInfo")
@NamedQueries({
    @NamedQuery(name = "AgDNGProxyInfo.findAll", query = "SELECT a FROM AgDNGProxyInfo a"),
    @NamedQuery(name = "AgDNGProxyInfo.findByIdLocal", query = "SELECT a FROM AgDNGProxyInfo a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgDNGProxyInfo.findByFileUUID", query = "SELECT a FROM AgDNGProxyInfo a WHERE a.fileUUID = :fileUUID"),
    @NamedQuery(name = "AgDNGProxyInfo.findByStatus", query = "SELECT a FROM AgDNGProxyInfo a WHERE a.status = :status"),
    @NamedQuery(name = "AgDNGProxyInfo.findByStatusDateTime", query = "SELECT a FROM AgDNGProxyInfo a WHERE a.statusDateTime = :statusDateTime")})
public class AgDNGProxyInfo implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "fileUUID")
    private String fileUUID;
    @Basic(optional = false)
    @Column(name = "status")
    private String status;
    @Basic(optional = false)
    @Column(name = "statusDateTime")
    private String statusDateTime;

    public AgDNGProxyInfo() {
    }

    public AgDNGProxyInfo(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgDNGProxyInfo(Integer idLocal, String fileUUID, String status, String statusDateTime) {
        this.idLocal = idLocal;
        this.fileUUID = fileUUID;
        this.status = status;
        this.statusDateTime = statusDateTime;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getFileUUID() {
        return fileUUID;
    }

    public void setFileUUID(String fileUUID) {
        this.fileUUID = fileUUID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDateTime() {
        return statusDateTime;
    }

    public void setStatusDateTime(String statusDateTime) {
        this.statusDateTime = statusDateTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idLocal != null ? idLocal.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AgDNGProxyInfo)) {
            return false;
        }
        AgDNGProxyInfo other = (AgDNGProxyInfo) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgDNGProxyInfo[ idLocal=" + idLocal + " ]";
    }
  }
