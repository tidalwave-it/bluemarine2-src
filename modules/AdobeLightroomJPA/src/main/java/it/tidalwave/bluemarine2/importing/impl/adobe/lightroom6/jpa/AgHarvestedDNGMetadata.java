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
@Table(name = "AgHarvestedDNGMetadata")
@NamedQueries({
    @NamedQuery(name = "AgHarvestedDNGMetadata.findAll", query = "SELECT a FROM AgHarvestedDNGMetadata a"),
    @NamedQuery(name = "AgHarvestedDNGMetadata.findByIdLocal", query = "SELECT a FROM AgHarvestedDNGMetadata a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgHarvestedDNGMetadata.findByImage", query = "SELECT a FROM AgHarvestedDNGMetadata a WHERE a.image = :image"),
    @NamedQuery(name = "AgHarvestedDNGMetadata.findByHasFastLoadData", query = "SELECT a FROM AgHarvestedDNGMetadata a WHERE a.hasFastLoadData = :hasFastLoadData"),
    @NamedQuery(name = "AgHarvestedDNGMetadata.findByHasLossyCompression", query = "SELECT a FROM AgHarvestedDNGMetadata a WHERE a.hasLossyCompression = :hasLossyCompression"),
    @NamedQuery(name = "AgHarvestedDNGMetadata.findByIsDNG", query = "SELECT a FROM AgHarvestedDNGMetadata a WHERE a.isDNG = :isDNG"),
    @NamedQuery(name = "AgHarvestedDNGMetadata.findByIsHDR", query = "SELECT a FROM AgHarvestedDNGMetadata a WHERE a.isHDR = :isHDR"),
    @NamedQuery(name = "AgHarvestedDNGMetadata.findByIsPano", query = "SELECT a FROM AgHarvestedDNGMetadata a WHERE a.isPano = :isPano"),
    @NamedQuery(name = "AgHarvestedDNGMetadata.findByIsReducedResolution", query = "SELECT a FROM AgHarvestedDNGMetadata a WHERE a.isReducedResolution = :isReducedResolution")})
public class AgHarvestedDNGMetadata implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Column(name = "image")
    private Integer image;
    @Column(name = "hasFastLoadData")
    private Integer hasFastLoadData;
    @Column(name = "hasLossyCompression")
    private Integer hasLossyCompression;
    @Column(name = "isDNG")
    private Integer isDNG;
    @Column(name = "isHDR")
    private Integer isHDR;
    @Column(name = "isPano")
    private Integer isPano;
    @Column(name = "isReducedResolution")
    private Integer isReducedResolution;

    public AgHarvestedDNGMetadata() {
    }

    public AgHarvestedDNGMetadata(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public Integer getHasFastLoadData() {
        return hasFastLoadData;
    }

    public void setHasFastLoadData(Integer hasFastLoadData) {
        this.hasFastLoadData = hasFastLoadData;
    }

    public Integer getHasLossyCompression() {
        return hasLossyCompression;
    }

    public void setHasLossyCompression(Integer hasLossyCompression) {
        this.hasLossyCompression = hasLossyCompression;
    }

    public Integer getIsDNG() {
        return isDNG;
    }

    public void setIsDNG(Integer isDNG) {
        this.isDNG = isDNG;
    }

    public Integer getIsHDR() {
        return isHDR;
    }

    public void setIsHDR(Integer isHDR) {
        this.isHDR = isHDR;
    }

    public Integer getIsPano() {
        return isPano;
    }

    public void setIsPano(Integer isPano) {
        this.isPano = isPano;
    }

    public Integer getIsReducedResolution() {
        return isReducedResolution;
    }

    public void setIsReducedResolution(Integer isReducedResolution) {
        this.isReducedResolution = isReducedResolution;
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
        if (!(object instanceof AgHarvestedDNGMetadata)) {
            return false;
        }
        AgHarvestedDNGMetadata other = (AgHarvestedDNGMetadata) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgHarvestedDNGMetadata[ idLocal=" + idLocal + " ]";
    }
  }
