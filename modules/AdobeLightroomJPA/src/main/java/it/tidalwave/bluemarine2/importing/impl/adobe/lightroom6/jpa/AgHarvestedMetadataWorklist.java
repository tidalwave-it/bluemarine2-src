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
@Table(name = "AgHarvestedMetadataWorklist")
@NamedQueries({
    @NamedQuery(name = "AgHarvestedMetadataWorklist.findAll", query = "SELECT a FROM AgHarvestedMetadataWorklist a"),
    @NamedQuery(name = "AgHarvestedMetadataWorklist.findByIdLocal", query = "SELECT a FROM AgHarvestedMetadataWorklist a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgHarvestedMetadataWorklist.findByTaskID", query = "SELECT a FROM AgHarvestedMetadataWorklist a WHERE a.taskID = :taskID"),
    @NamedQuery(name = "AgHarvestedMetadataWorklist.findByTaskStatus", query = "SELECT a FROM AgHarvestedMetadataWorklist a WHERE a.taskStatus = :taskStatus"),
    @NamedQuery(name = "AgHarvestedMetadataWorklist.findByWhenPosted", query = "SELECT a FROM AgHarvestedMetadataWorklist a WHERE a.whenPosted = :whenPosted")})
public class AgHarvestedMetadataWorklist implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "taskID")
    private String taskID;
    @Basic(optional = false)
    @Column(name = "taskStatus")
    private String taskStatus;
    @Basic(optional = false)
    @Column(name = "whenPosted")
    private String whenPosted;

    public AgHarvestedMetadataWorklist() {
    }

    public AgHarvestedMetadataWorklist(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgHarvestedMetadataWorklist(Integer idLocal, String taskID, String taskStatus, String whenPosted) {
        this.idLocal = idLocal;
        this.taskID = taskID;
        this.taskStatus = taskStatus;
        this.whenPosted = whenPosted;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getWhenPosted() {
        return whenPosted;
    }

    public void setWhenPosted(String whenPosted) {
        this.whenPosted = whenPosted;
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
        if (!(object instanceof AgHarvestedMetadataWorklist)) {
            return false;
        }
        AgHarvestedMetadataWorklist other = (AgHarvestedMetadataWorklist) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgHarvestedMetadataWorklist[ idLocal=" + idLocal + " ]";
    }
  }
