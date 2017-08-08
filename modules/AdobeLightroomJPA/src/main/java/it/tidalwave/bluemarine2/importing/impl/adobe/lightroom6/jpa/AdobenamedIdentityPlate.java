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
@Table(name = "Adobe_namedIdentityPlate")
@NamedQueries({
    @NamedQuery(name = "AdobenamedIdentityPlate.findAll", query = "SELECT a FROM AdobenamedIdentityPlate a"),
    @NamedQuery(name = "AdobenamedIdentityPlate.findByIdLocal", query = "SELECT a FROM AdobenamedIdentityPlate a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AdobenamedIdentityPlate.findByIdGlobal", query = "SELECT a FROM AdobenamedIdentityPlate a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "AdobenamedIdentityPlate.findByDescription", query = "SELECT a FROM AdobenamedIdentityPlate a WHERE a.description = :description"),
    @NamedQuery(name = "AdobenamedIdentityPlate.findByIdentityPlate", query = "SELECT a FROM AdobenamedIdentityPlate a WHERE a.identityPlate = :identityPlate"),
    @NamedQuery(name = "AdobenamedIdentityPlate.findByIdentityPlateHash", query = "SELECT a FROM AdobenamedIdentityPlate a WHERE a.identityPlateHash = :identityPlateHash"),
    @NamedQuery(name = "AdobenamedIdentityPlate.findByModuleFont", query = "SELECT a FROM AdobenamedIdentityPlate a WHERE a.moduleFont = :moduleFont"),
    @NamedQuery(name = "AdobenamedIdentityPlate.findByModuleSelectedTextColor", query = "SELECT a FROM AdobenamedIdentityPlate a WHERE a.moduleSelectedTextColor = :moduleSelectedTextColor"),
    @NamedQuery(name = "AdobenamedIdentityPlate.findByModuleTextColor", query = "SELECT a FROM AdobenamedIdentityPlate a WHERE a.moduleTextColor = :moduleTextColor")})
public class AdobenamedIdentityPlate implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Column(name = "description")
    private String description;
    @Column(name = "identityPlate")
    private String identityPlate;
    @Column(name = "identityPlateHash")
    private String identityPlateHash;
    @Column(name = "moduleFont")
    private String moduleFont;
    @Column(name = "moduleSelectedTextColor")
    private String moduleSelectedTextColor;
    @Column(name = "moduleTextColor")
    private String moduleTextColor;

    public AdobenamedIdentityPlate() {
    }

    public AdobenamedIdentityPlate(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AdobenamedIdentityPlate(Integer idLocal, String idGlobal) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentityPlate() {
        return identityPlate;
    }

    public void setIdentityPlate(String identityPlate) {
        this.identityPlate = identityPlate;
    }

    public String getIdentityPlateHash() {
        return identityPlateHash;
    }

    public void setIdentityPlateHash(String identityPlateHash) {
        this.identityPlateHash = identityPlateHash;
    }

    public String getModuleFont() {
        return moduleFont;
    }

    public void setModuleFont(String moduleFont) {
        this.moduleFont = moduleFont;
    }

    public String getModuleSelectedTextColor() {
        return moduleSelectedTextColor;
    }

    public void setModuleSelectedTextColor(String moduleSelectedTextColor) {
        this.moduleSelectedTextColor = moduleSelectedTextColor;
    }

    public String getModuleTextColor() {
        return moduleTextColor;
    }

    public void setModuleTextColor(String moduleTextColor) {
        this.moduleTextColor = moduleTextColor;
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
        if (!(object instanceof AdobenamedIdentityPlate)) {
            return false;
        }
        AdobenamedIdentityPlate other = (AdobenamedIdentityPlate) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AdobenamedIdentityPlate[ idLocal=" + idLocal + " ]";
    }
  }
