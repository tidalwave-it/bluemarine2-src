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
@Table(name = "Adobe_imageDevelopBeforeSettings")
@NamedQueries({
    @NamedQuery(name = "AdobeimageDevelopBeforeSettings.findAll", query = "SELECT a FROM AdobeimageDevelopBeforeSettings a"),
    @NamedQuery(name = "AdobeimageDevelopBeforeSettings.findByIdLocal", query = "SELECT a FROM AdobeimageDevelopBeforeSettings a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AdobeimageDevelopBeforeSettings.findByBeforeDigest", query = "SELECT a FROM AdobeimageDevelopBeforeSettings a WHERE a.beforeDigest = :beforeDigest"),
    @NamedQuery(name = "AdobeimageDevelopBeforeSettings.findByBeforeHasDevelopAdjustments", query = "SELECT a FROM AdobeimageDevelopBeforeSettings a WHERE a.beforeHasDevelopAdjustments = :beforeHasDevelopAdjustments"),
    @NamedQuery(name = "AdobeimageDevelopBeforeSettings.findByBeforePresetID", query = "SELECT a FROM AdobeimageDevelopBeforeSettings a WHERE a.beforePresetID = :beforePresetID"),
    @NamedQuery(name = "AdobeimageDevelopBeforeSettings.findByBeforeText", query = "SELECT a FROM AdobeimageDevelopBeforeSettings a WHERE a.beforeText = :beforeText"),
    @NamedQuery(name = "AdobeimageDevelopBeforeSettings.findByDevelopSettings", query = "SELECT a FROM AdobeimageDevelopBeforeSettings a WHERE a.developSettings = :developSettings")})
public class AdobeimageDevelopBeforeSettings implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Column(name = "beforeDigest")
    private String beforeDigest;
    @Column(name = "beforeHasDevelopAdjustments")
    private String beforeHasDevelopAdjustments;
    @Column(name = "beforePresetID")
    private String beforePresetID;
    @Column(name = "beforeText")
    private String beforeText;
    @Column(name = "developSettings")
    private Integer developSettings;

    public AdobeimageDevelopBeforeSettings() {
    }

    public AdobeimageDevelopBeforeSettings(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getBeforeDigest() {
        return beforeDigest;
    }

    public void setBeforeDigest(String beforeDigest) {
        this.beforeDigest = beforeDigest;
    }

    public String getBeforeHasDevelopAdjustments() {
        return beforeHasDevelopAdjustments;
    }

    public void setBeforeHasDevelopAdjustments(String beforeHasDevelopAdjustments) {
        this.beforeHasDevelopAdjustments = beforeHasDevelopAdjustments;
    }

    public String getBeforePresetID() {
        return beforePresetID;
    }

    public void setBeforePresetID(String beforePresetID) {
        this.beforePresetID = beforePresetID;
    }

    public String getBeforeText() {
        return beforeText;
    }

    public void setBeforeText(String beforeText) {
        this.beforeText = beforeText;
    }

    public Integer getDevelopSettings() {
        return developSettings;
    }

    public void setDevelopSettings(Integer developSettings) {
        this.developSettings = developSettings;
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
        if (!(object instanceof AdobeimageDevelopBeforeSettings)) {
            return false;
        }
        AdobeimageDevelopBeforeSettings other = (AdobeimageDevelopBeforeSettings) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AdobeimageDevelopBeforeSettings[ idLocal=" + idLocal + " ]";
    }
  }
