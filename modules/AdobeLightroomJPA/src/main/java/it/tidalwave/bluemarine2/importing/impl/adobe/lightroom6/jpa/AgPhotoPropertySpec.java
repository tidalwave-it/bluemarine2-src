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
@Table(name = "AgPhotoPropertySpec")
@NamedQueries({
    @NamedQuery(name = "AgPhotoPropertySpec.findAll", query = "SELECT a FROM AgPhotoPropertySpec a"),
    @NamedQuery(name = "AgPhotoPropertySpec.findByIdLocal", query = "SELECT a FROM AgPhotoPropertySpec a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgPhotoPropertySpec.findByIdGlobal", query = "SELECT a FROM AgPhotoPropertySpec a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "AgPhotoPropertySpec.findByFlattenedAttributes", query = "SELECT a FROM AgPhotoPropertySpec a WHERE a.flattenedAttributes = :flattenedAttributes"),
    @NamedQuery(name = "AgPhotoPropertySpec.findByKey", query = "SELECT a FROM AgPhotoPropertySpec a WHERE a.key = :key"),
    @NamedQuery(name = "AgPhotoPropertySpec.findByPluginVersion", query = "SELECT a FROM AgPhotoPropertySpec a WHERE a.pluginVersion = :pluginVersion"),
    @NamedQuery(name = "AgPhotoPropertySpec.findBySourcePlugin", query = "SELECT a FROM AgPhotoPropertySpec a WHERE a.sourcePlugin = :sourcePlugin"),
    @NamedQuery(name = "AgPhotoPropertySpec.findByUserVisibleName", query = "SELECT a FROM AgPhotoPropertySpec a WHERE a.userVisibleName = :userVisibleName")})
public class AgPhotoPropertySpec implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Column(name = "flattenedAttributes")
    private String flattenedAttributes;
    @Basic(optional = false)
    @Column(name = "key")
    private String key;
    @Basic(optional = false)
    @Column(name = "pluginVersion")
    private String pluginVersion;
    @Basic(optional = false)
    @Column(name = "sourcePlugin")
    private String sourcePlugin;
    @Column(name = "userVisibleName")
    private String userVisibleName;

    public AgPhotoPropertySpec() {
    }

    public AgPhotoPropertySpec(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgPhotoPropertySpec(Integer idLocal, String idGlobal, String key, String pluginVersion, String sourcePlugin) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
        this.key = key;
        this.pluginVersion = pluginVersion;
        this.sourcePlugin = sourcePlugin;
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

    public String getFlattenedAttributes() {
        return flattenedAttributes;
    }

    public void setFlattenedAttributes(String flattenedAttributes) {
        this.flattenedAttributes = flattenedAttributes;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public String getSourcePlugin() {
        return sourcePlugin;
    }

    public void setSourcePlugin(String sourcePlugin) {
        this.sourcePlugin = sourcePlugin;
    }

    public String getUserVisibleName() {
        return userVisibleName;
    }

    public void setUserVisibleName(String userVisibleName) {
        this.userVisibleName = userVisibleName;
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
        if (!(object instanceof AgPhotoPropertySpec)) {
            return false;
        }
        AgPhotoPropertySpec other = (AgPhotoPropertySpec) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgPhotoPropertySpec[ idLocal=" + idLocal + " ]";
    }
  }
