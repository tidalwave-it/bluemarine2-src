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
@Table(name = "AgUnsupportedOzAssets")
@NamedQueries({
    @NamedQuery(name = "AgUnsupportedOzAssets.findAll", query = "SELECT a FROM AgUnsupportedOzAssets a"),
    @NamedQuery(name = "AgUnsupportedOzAssets.findByIdLocal", query = "SELECT a FROM AgUnsupportedOzAssets a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgUnsupportedOzAssets.findByOzAssetId", query = "SELECT a FROM AgUnsupportedOzAssets a WHERE a.ozAssetId = :ozAssetId"),
    @NamedQuery(name = "AgUnsupportedOzAssets.findByOzCatalogId", query = "SELECT a FROM AgUnsupportedOzAssets a WHERE a.ozCatalogId = :ozCatalogId"),
    @NamedQuery(name = "AgUnsupportedOzAssets.findByPath", query = "SELECT a FROM AgUnsupportedOzAssets a WHERE a.path = :path"),
    @NamedQuery(name = "AgUnsupportedOzAssets.findByType", query = "SELECT a FROM AgUnsupportedOzAssets a WHERE a.type = :type"),
    @NamedQuery(name = "AgUnsupportedOzAssets.findByPayload", query = "SELECT a FROM AgUnsupportedOzAssets a WHERE a.payload = :payload")})
public class AgUnsupportedOzAssets implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "ozAssetId")
    private String ozAssetId;
    @Basic(optional = false)
    @Column(name = "ozCatalogId")
    private String ozCatalogId;
    @Basic(optional = false)
    @Column(name = "path")
    private String path;
    @Basic(optional = false)
    @Column(name = "type")
    private String type;
    @Basic(optional = false)
    @Column(name = "payload")
    private String payload;

    public AgUnsupportedOzAssets() {
    }

    public AgUnsupportedOzAssets(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgUnsupportedOzAssets(Integer idLocal, String ozAssetId, String ozCatalogId, String path, String type, String payload) {
        this.idLocal = idLocal;
        this.ozAssetId = ozAssetId;
        this.ozCatalogId = ozCatalogId;
        this.path = path;
        this.type = type;
        this.payload = payload;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getOzAssetId() {
        return ozAssetId;
    }

    public void setOzAssetId(String ozAssetId) {
        this.ozAssetId = ozAssetId;
    }

    public String getOzCatalogId() {
        return ozCatalogId;
    }

    public void setOzCatalogId(String ozCatalogId) {
        this.ozCatalogId = ozCatalogId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
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
        if (!(object instanceof AgUnsupportedOzAssets)) {
            return false;
        }
        AgUnsupportedOzAssets other = (AgUnsupportedOzAssets) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgUnsupportedOzAssets[ idLocal=" + idLocal + " ]";
    }
  }
