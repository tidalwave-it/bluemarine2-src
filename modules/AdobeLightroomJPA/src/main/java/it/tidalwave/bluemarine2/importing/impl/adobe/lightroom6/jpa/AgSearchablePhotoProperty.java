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
@Table(name = "AgSearchablePhotoProperty")
@NamedQueries({
    @NamedQuery(name = "AgSearchablePhotoProperty.findAll", query = "SELECT a FROM AgSearchablePhotoProperty a"),
    @NamedQuery(name = "AgSearchablePhotoProperty.findByIdLocal", query = "SELECT a FROM AgSearchablePhotoProperty a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgSearchablePhotoProperty.findByIdGlobal", query = "SELECT a FROM AgSearchablePhotoProperty a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "AgSearchablePhotoProperty.findByDataType", query = "SELECT a FROM AgSearchablePhotoProperty a WHERE a.dataType = :dataType"),
    @NamedQuery(name = "AgSearchablePhotoProperty.findByInternalValue", query = "SELECT a FROM AgSearchablePhotoProperty a WHERE a.internalValue = :internalValue"),
    @NamedQuery(name = "AgSearchablePhotoProperty.findByLcidxinternalValue", query = "SELECT a FROM AgSearchablePhotoProperty a WHERE a.lcidxinternalValue = :lcidxinternalValue"),
    @NamedQuery(name = "AgSearchablePhotoProperty.findByPhoto", query = "SELECT a FROM AgSearchablePhotoProperty a WHERE a.photo = :photo"),
    @NamedQuery(name = "AgSearchablePhotoProperty.findByPropertySpec", query = "SELECT a FROM AgSearchablePhotoProperty a WHERE a.propertySpec = :propertySpec")})
public class AgSearchablePhotoProperty implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Column(name = "dataType")
    private String dataType;
    @Column(name = "internalValue")
    private String internalValue;
    @Column(name = "lc_idx_internalValue")
    private String lcidxinternalValue;
    @Basic(optional = false)
    @Column(name = "photo")
    private int photo;
    @Basic(optional = false)
    @Column(name = "propertySpec")
    private int propertySpec;

    public AgSearchablePhotoProperty() {
    }

    public AgSearchablePhotoProperty(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgSearchablePhotoProperty(Integer idLocal, String idGlobal, int photo, int propertySpec) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
        this.photo = photo;
        this.propertySpec = propertySpec;
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getInternalValue() {
        return internalValue;
    }

    public void setInternalValue(String internalValue) {
        this.internalValue = internalValue;
    }

    public String getLcidxinternalValue() {
        return lcidxinternalValue;
    }

    public void setLcidxinternalValue(String lcidxinternalValue) {
        this.lcidxinternalValue = lcidxinternalValue;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public int getPropertySpec() {
        return propertySpec;
    }

    public void setPropertySpec(int propertySpec) {
        this.propertySpec = propertySpec;
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
        if (!(object instanceof AgSearchablePhotoProperty)) {
            return false;
        }
        AgSearchablePhotoProperty other = (AgSearchablePhotoProperty) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgSearchablePhotoProperty[ idLocal=" + idLocal + " ]";
    }
  }
