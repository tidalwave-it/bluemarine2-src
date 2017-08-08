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
@Table(name = "Adobe_imageProofSettings")
@NamedQueries({
    @NamedQuery(name = "AdobeimageProofSettings.findAll", query = "SELECT a FROM AdobeimageProofSettings a"),
    @NamedQuery(name = "AdobeimageProofSettings.findByIdLocal", query = "SELECT a FROM AdobeimageProofSettings a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AdobeimageProofSettings.findByColorProfile", query = "SELECT a FROM AdobeimageProofSettings a WHERE a.colorProfile = :colorProfile"),
    @NamedQuery(name = "AdobeimageProofSettings.findByImage", query = "SELECT a FROM AdobeimageProofSettings a WHERE a.image = :image"),
    @NamedQuery(name = "AdobeimageProofSettings.findByRenderingIntent", query = "SELECT a FROM AdobeimageProofSettings a WHERE a.renderingIntent = :renderingIntent")})
public class AdobeimageProofSettings implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Column(name = "colorProfile")
    private String colorProfile;
    @Column(name = "image")
    private Integer image;
    @Column(name = "renderingIntent")
    private String renderingIntent;

    public AdobeimageProofSettings() {
    }

    public AdobeimageProofSettings(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getColorProfile() {
        return colorProfile;
    }

    public void setColorProfile(String colorProfile) {
        this.colorProfile = colorProfile;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getRenderingIntent() {
        return renderingIntent;
    }

    public void setRenderingIntent(String renderingIntent) {
        this.renderingIntent = renderingIntent;
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
        if (!(object instanceof AdobeimageProofSettings)) {
            return false;
        }
        AdobeimageProofSettings other = (AdobeimageProofSettings) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AdobeimageProofSettings[ idLocal=" + idLocal + " ]";
    }
  }
