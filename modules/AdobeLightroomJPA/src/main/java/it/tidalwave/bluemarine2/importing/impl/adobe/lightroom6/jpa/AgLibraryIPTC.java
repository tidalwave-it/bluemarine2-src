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
@Table(name = "AgLibraryIPTC")
@NamedQueries({
    @NamedQuery(name = "AgLibraryIPTC.findAll", query = "SELECT a FROM AgLibraryIPTC a"),
    @NamedQuery(name = "AgLibraryIPTC.findByIdLocal", query = "SELECT a FROM AgLibraryIPTC a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryIPTC.findByCaption", query = "SELECT a FROM AgLibraryIPTC a WHERE a.caption = :caption"),
    @NamedQuery(name = "AgLibraryIPTC.findByCopyright", query = "SELECT a FROM AgLibraryIPTC a WHERE a.copyright = :copyright"),
    @NamedQuery(name = "AgLibraryIPTC.findByImage", query = "SELECT a FROM AgLibraryIPTC a WHERE a.image = :image")})
public class AgLibraryIPTC implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Column(name = "caption")
    private String caption;
    @Column(name = "copyright")
    private String copyright;
    @Basic(optional = false)
    @Column(name = "image")
    private int image;

    public AgLibraryIPTC() {
    }

    public AgLibraryIPTC(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryIPTC(Integer idLocal, int image) {
        this.idLocal = idLocal;
        this.image = image;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
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
        if (!(object instanceof AgLibraryIPTC)) {
            return false;
        }
        AgLibraryIPTC other = (AgLibraryIPTC) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryIPTC[ idLocal=" + idLocal + " ]";
    }
  }
