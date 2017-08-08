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
@Table(name = "Adobe_variablesTable")
@NamedQueries({
    @NamedQuery(name = "AdobevariablesTable.findAll", query = "SELECT a FROM AdobevariablesTable a"),
    @NamedQuery(name = "AdobevariablesTable.findByIdLocal", query = "SELECT a FROM AdobevariablesTable a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AdobevariablesTable.findByIdGlobal", query = "SELECT a FROM AdobevariablesTable a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "AdobevariablesTable.findByName", query = "SELECT a FROM AdobevariablesTable a WHERE a.name = :name"),
    @NamedQuery(name = "AdobevariablesTable.findByType", query = "SELECT a FROM AdobevariablesTable a WHERE a.type = :type"),
    @NamedQuery(name = "AdobevariablesTable.findByValue", query = "SELECT a FROM AdobevariablesTable a WHERE a.value = :value")})
public class AdobevariablesTable implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private String type;
    @Basic(optional = false)
    @Column(name = "value")
    private String value;

    public AdobevariablesTable() {
    }

    public AdobevariablesTable(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AdobevariablesTable(Integer idLocal, String idGlobal, String value) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
        this.value = value;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getIdGlobal() {
        return idGlobal;
    }

    public void setIdGlobal(String idGlobal) {
        this.idGlobal = idGlobal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        if (!(object instanceof AdobevariablesTable)) {
            return false;
        }
        AdobevariablesTable other = (AdobevariablesTable) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AdobevariablesTable[ idLocal=" + idLocal + " ]";
    }
  }
