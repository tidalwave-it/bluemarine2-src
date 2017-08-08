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
@Table(name = "AgMRULists")
@NamedQueries({
    @NamedQuery(name = "AgMRULists.findAll", query = "SELECT a FROM AgMRULists a"),
    @NamedQuery(name = "AgMRULists.findByIdLocal", query = "SELECT a FROM AgMRULists a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgMRULists.findByListID", query = "SELECT a FROM AgMRULists a WHERE a.listID = :listID"),
    @NamedQuery(name = "AgMRULists.findByTimestamp", query = "SELECT a FROM AgMRULists a WHERE a.timestamp = :timestamp"),
    @NamedQuery(name = "AgMRULists.findByValue", query = "SELECT a FROM AgMRULists a WHERE a.value = :value")})
public class AgMRULists implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "listID")
    private String listID;
    @Basic(optional = false)
    @Column(name = "timestamp")
    private String timestamp;
    @Basic(optional = false)
    @Column(name = "value")
    private String value;

    public AgMRULists() {
    }

    public AgMRULists(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgMRULists(Integer idLocal, String listID, String timestamp, String value) {
        this.idLocal = idLocal;
        this.listID = listID;
        this.timestamp = timestamp;
        this.value = value;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        if (!(object instanceof AgMRULists)) {
            return false;
        }
        AgMRULists other = (AgMRULists) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgMRULists[ idLocal=" + idLocal + " ]";
    }
  }
