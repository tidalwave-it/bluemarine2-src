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
@Table(name = "AgMetadataSearchIndex")
@NamedQueries({
    @NamedQuery(name = "AgMetadataSearchIndex.findAll", query = "SELECT a FROM AgMetadataSearchIndex a"),
    @NamedQuery(name = "AgMetadataSearchIndex.findByIdLocal", query = "SELECT a FROM AgMetadataSearchIndex a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgMetadataSearchIndex.findByExifSearchIndex", query = "SELECT a FROM AgMetadataSearchIndex a WHERE a.exifSearchIndex = :exifSearchIndex"),
    @NamedQuery(name = "AgMetadataSearchIndex.findByImage", query = "SELECT a FROM AgMetadataSearchIndex a WHERE a.image = :image"),
    @NamedQuery(name = "AgMetadataSearchIndex.findByIptcSearchIndex", query = "SELECT a FROM AgMetadataSearchIndex a WHERE a.iptcSearchIndex = :iptcSearchIndex"),
    @NamedQuery(name = "AgMetadataSearchIndex.findByOtherSearchIndex", query = "SELECT a FROM AgMetadataSearchIndex a WHERE a.otherSearchIndex = :otherSearchIndex"),
    @NamedQuery(name = "AgMetadataSearchIndex.findBySearchIndex", query = "SELECT a FROM AgMetadataSearchIndex a WHERE a.searchIndex = :searchIndex")})
public class AgMetadataSearchIndex implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "exifSearchIndex")
    private String exifSearchIndex;
    @Column(name = "image")
    private Integer image;
    @Basic(optional = false)
    @Column(name = "iptcSearchIndex")
    private String iptcSearchIndex;
    @Basic(optional = false)
    @Column(name = "otherSearchIndex")
    private String otherSearchIndex;
    @Basic(optional = false)
    @Column(name = "searchIndex")
    private String searchIndex;

    public AgMetadataSearchIndex() {
    }

    public AgMetadataSearchIndex(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgMetadataSearchIndex(Integer idLocal, String exifSearchIndex, String iptcSearchIndex, String otherSearchIndex, String searchIndex) {
        this.idLocal = idLocal;
        this.exifSearchIndex = exifSearchIndex;
        this.iptcSearchIndex = iptcSearchIndex;
        this.otherSearchIndex = otherSearchIndex;
        this.searchIndex = searchIndex;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getExifSearchIndex() {
        return exifSearchIndex;
    }

    public void setExifSearchIndex(String exifSearchIndex) {
        this.exifSearchIndex = exifSearchIndex;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getIptcSearchIndex() {
        return iptcSearchIndex;
    }

    public void setIptcSearchIndex(String iptcSearchIndex) {
        this.iptcSearchIndex = iptcSearchIndex;
    }

    public String getOtherSearchIndex() {
        return otherSearchIndex;
    }

    public void setOtherSearchIndex(String otherSearchIndex) {
        this.otherSearchIndex = otherSearchIndex;
    }

    public String getSearchIndex() {
        return searchIndex;
    }

    public void setSearchIndex(String searchIndex) {
        this.searchIndex = searchIndex;
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
        if (!(object instanceof AgMetadataSearchIndex)) {
            return false;
        }
        AgMetadataSearchIndex other = (AgMetadataSearchIndex) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgMetadataSearchIndex[ idLocal=" + idLocal + " ]";
    }
  }
