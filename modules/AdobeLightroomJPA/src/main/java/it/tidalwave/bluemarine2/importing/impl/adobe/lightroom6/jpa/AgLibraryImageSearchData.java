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
@Table(name = "AgLibraryImageSearchData")
@NamedQueries({
    @NamedQuery(name = "AgLibraryImageSearchData.findAll", query = "SELECT a FROM AgLibraryImageSearchData a"),
    @NamedQuery(name = "AgLibraryImageSearchData.findByIdLocal", query = "SELECT a FROM AgLibraryImageSearchData a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryImageSearchData.findByFeatInfo", query = "SELECT a FROM AgLibraryImageSearchData a WHERE a.featInfo = :featInfo"),
    @NamedQuery(name = "AgLibraryImageSearchData.findByHeight", query = "SELECT a FROM AgLibraryImageSearchData a WHERE a.height = :height"),
    @NamedQuery(name = "AgLibraryImageSearchData.findByIdDesc", query = "SELECT a FROM AgLibraryImageSearchData a WHERE a.idDesc = :idDesc"),
    @NamedQuery(name = "AgLibraryImageSearchData.findByIdDescCh", query = "SELECT a FROM AgLibraryImageSearchData a WHERE a.idDescCh = :idDescCh"),
    @NamedQuery(name = "AgLibraryImageSearchData.findByImage", query = "SELECT a FROM AgLibraryImageSearchData a WHERE a.image = :image"),
    @NamedQuery(name = "AgLibraryImageSearchData.findByWidth", query = "SELECT a FROM AgLibraryImageSearchData a WHERE a.width = :width")})
public class AgLibraryImageSearchData implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Column(name = "featInfo")
    private String featInfo;
    @Column(name = "height")
    private String height;
    @Column(name = "idDesc")
    private String idDesc;
    @Column(name = "idDescCh")
    private String idDescCh;
    @Basic(optional = false)
    @Column(name = "image")
    private int image;
    @Column(name = "width")
    private String width;

    public AgLibraryImageSearchData() {
    }

    public AgLibraryImageSearchData(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryImageSearchData(Integer idLocal, int image) {
        this.idLocal = idLocal;
        this.image = image;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getFeatInfo() {
        return featInfo;
    }

    public void setFeatInfo(String featInfo) {
        this.featInfo = featInfo;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getIdDesc() {
        return idDesc;
    }

    public void setIdDesc(String idDesc) {
        this.idDesc = idDesc;
    }

    public String getIdDescCh() {
        return idDescCh;
    }

    public void setIdDescCh(String idDescCh) {
        this.idDescCh = idDescCh;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
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
        if (!(object instanceof AgLibraryImageSearchData)) {
            return false;
        }
        AgLibraryImageSearchData other = (AgLibraryImageSearchData) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryImageSearchData[ idLocal=" + idLocal + " ]";
    }
  }
