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
@Table(name = "LrMobileSyncChangeCounter")
@NamedQueries({
    @NamedQuery(name = "LrMobileSyncChangeCounter.findAll", query = "SELECT l FROM LrMobileSyncChangeCounter l"),
    @NamedQuery(name = "LrMobileSyncChangeCounter.findById", query = "SELECT l FROM LrMobileSyncChangeCounter l WHERE l.id = :id"),
    @NamedQuery(name = "LrMobileSyncChangeCounter.findByChangeCounter", query = "SELECT l FROM LrMobileSyncChangeCounter l WHERE l.changeCounter = :changeCounter")})
public class LrMobileSyncChangeCounter implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private String id;
    @Basic(optional = false)
    @Column(name = "changeCounter")
    private String changeCounter;

    public LrMobileSyncChangeCounter() {
    }

    public LrMobileSyncChangeCounter(String id) {
        this.id = id;
    }

    public LrMobileSyncChangeCounter(String id, String changeCounter) {
        this.id = id;
        this.changeCounter = changeCounter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChangeCounter() {
        return changeCounter;
    }

    public void setChangeCounter(String changeCounter) {
        this.changeCounter = changeCounter;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LrMobileSyncChangeCounter)) {
            return false;
        }
        LrMobileSyncChangeCounter other = (LrMobileSyncChangeCounter) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.LrMobileSyncChangeCounter[ id=" + id + " ]";
    }
  }
