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
@Table(name = "AgLibraryImageChangeCounter")
@NamedQueries({
    @NamedQuery(name = "AgLibraryImageChangeCounter.findAll", query = "SELECT a FROM AgLibraryImageChangeCounter a"),
    @NamedQuery(name = "AgLibraryImageChangeCounter.findByImage", query = "SELECT a FROM AgLibraryImageChangeCounter a WHERE a.image = :image"),
    @NamedQuery(name = "AgLibraryImageChangeCounter.findByChangeCounter", query = "SELECT a FROM AgLibraryImageChangeCounter a WHERE a.changeCounter = :changeCounter"),
    @NamedQuery(name = "AgLibraryImageChangeCounter.findByChangedAtTime", query = "SELECT a FROM AgLibraryImageChangeCounter a WHERE a.changedAtTime = :changedAtTime"),
    @NamedQuery(name = "AgLibraryImageChangeCounter.findByLocalTimeOffsetSecs", query = "SELECT a FROM AgLibraryImageChangeCounter a WHERE a.localTimeOffsetSecs = :localTimeOffsetSecs")})
public class AgLibraryImageChangeCounter implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "image")
    private String image;
    @Column(name = "changeCounter")
    private String changeCounter;
    @Column(name = "changedAtTime")
    private String changedAtTime;
    @Column(name = "localTimeOffsetSecs")
    private String localTimeOffsetSecs;

    public AgLibraryImageChangeCounter() {
    }

    public AgLibraryImageChangeCounter(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChangeCounter() {
        return changeCounter;
    }

    public void setChangeCounter(String changeCounter) {
        this.changeCounter = changeCounter;
    }

    public String getChangedAtTime() {
        return changedAtTime;
    }

    public void setChangedAtTime(String changedAtTime) {
        this.changedAtTime = changedAtTime;
    }

    public String getLocalTimeOffsetSecs() {
        return localTimeOffsetSecs;
    }

    public void setLocalTimeOffsetSecs(String localTimeOffsetSecs) {
        this.localTimeOffsetSecs = localTimeOffsetSecs;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (image != null ? image.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AgLibraryImageChangeCounter)) {
            return false;
        }
        AgLibraryImageChangeCounter other = (AgLibraryImageChangeCounter) object;
        if ((this.image == null && other.image != null) || (this.image != null && !this.image.equals(other.image))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryImageChangeCounter[ image=" + image + " ]";
    }
  }
