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
@Table(name = "AgSourceColorProfileConstants")
@NamedQueries({
    @NamedQuery(name = "AgSourceColorProfileConstants.findAll", query = "SELECT a FROM AgSourceColorProfileConstants a"),
    @NamedQuery(name = "AgSourceColorProfileConstants.findByIdLocal", query = "SELECT a FROM AgSourceColorProfileConstants a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgSourceColorProfileConstants.findByImage", query = "SELECT a FROM AgSourceColorProfileConstants a WHERE a.image = :image"),
    @NamedQuery(name = "AgSourceColorProfileConstants.findByProfileName", query = "SELECT a FROM AgSourceColorProfileConstants a WHERE a.profileName = :profileName")})
public class AgSourceColorProfileConstants implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "image")
    private int image;
    @Basic(optional = false)
    @Column(name = "profileName")
    private String profileName;

    public AgSourceColorProfileConstants() {
    }

    public AgSourceColorProfileConstants(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgSourceColorProfileConstants(Integer idLocal, int image, String profileName) {
        this.idLocal = idLocal;
        this.image = image;
        this.profileName = profileName;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
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
        if (!(object instanceof AgSourceColorProfileConstants)) {
            return false;
        }
        AgSourceColorProfileConstants other = (AgSourceColorProfileConstants) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgSourceColorProfileConstants[ idLocal=" + idLocal + " ]";
    }
  }
