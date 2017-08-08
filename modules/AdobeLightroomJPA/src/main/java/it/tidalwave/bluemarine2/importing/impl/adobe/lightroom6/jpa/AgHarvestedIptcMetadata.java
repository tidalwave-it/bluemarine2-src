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
@Table(name = "AgHarvestedIptcMetadata")
@NamedQueries({
    @NamedQuery(name = "AgHarvestedIptcMetadata.findAll", query = "SELECT a FROM AgHarvestedIptcMetadata a"),
    @NamedQuery(name = "AgHarvestedIptcMetadata.findByIdLocal", query = "SELECT a FROM AgHarvestedIptcMetadata a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgHarvestedIptcMetadata.findByImage", query = "SELECT a FROM AgHarvestedIptcMetadata a WHERE a.image = :image"),
    @NamedQuery(name = "AgHarvestedIptcMetadata.findByCityRef", query = "SELECT a FROM AgHarvestedIptcMetadata a WHERE a.cityRef = :cityRef"),
    @NamedQuery(name = "AgHarvestedIptcMetadata.findByCopyrightState", query = "SELECT a FROM AgHarvestedIptcMetadata a WHERE a.copyrightState = :copyrightState"),
    @NamedQuery(name = "AgHarvestedIptcMetadata.findByCountryRef", query = "SELECT a FROM AgHarvestedIptcMetadata a WHERE a.countryRef = :countryRef"),
    @NamedQuery(name = "AgHarvestedIptcMetadata.findByCreatorRef", query = "SELECT a FROM AgHarvestedIptcMetadata a WHERE a.creatorRef = :creatorRef"),
    @NamedQuery(name = "AgHarvestedIptcMetadata.findByIsoCountryCodeRef", query = "SELECT a FROM AgHarvestedIptcMetadata a WHERE a.isoCountryCodeRef = :isoCountryCodeRef"),
    @NamedQuery(name = "AgHarvestedIptcMetadata.findByJobIdentifierRef", query = "SELECT a FROM AgHarvestedIptcMetadata a WHERE a.jobIdentifierRef = :jobIdentifierRef"),
    @NamedQuery(name = "AgHarvestedIptcMetadata.findByLocationDataOrigination", query = "SELECT a FROM AgHarvestedIptcMetadata a WHERE a.locationDataOrigination = :locationDataOrigination"),
    @NamedQuery(name = "AgHarvestedIptcMetadata.findByLocationGPSSequence", query = "SELECT a FROM AgHarvestedIptcMetadata a WHERE a.locationGPSSequence = :locationGPSSequence"),
    @NamedQuery(name = "AgHarvestedIptcMetadata.findByLocationRef", query = "SELECT a FROM AgHarvestedIptcMetadata a WHERE a.locationRef = :locationRef"),
    @NamedQuery(name = "AgHarvestedIptcMetadata.findByStateRef", query = "SELECT a FROM AgHarvestedIptcMetadata a WHERE a.stateRef = :stateRef")})
public class AgHarvestedIptcMetadata implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Column(name = "image")
    private Integer image;
    @Column(name = "cityRef")
    private Integer cityRef;
    @Column(name = "copyrightState")
    private Integer copyrightState;
    @Column(name = "countryRef")
    private Integer countryRef;
    @Column(name = "creatorRef")
    private Integer creatorRef;
    @Column(name = "isoCountryCodeRef")
    private Integer isoCountryCodeRef;
    @Column(name = "jobIdentifierRef")
    private Integer jobIdentifierRef;
    @Basic(optional = false)
    @Column(name = "locationDataOrigination")
    private String locationDataOrigination;
    @Basic(optional = false)
    @Column(name = "locationGPSSequence")
    private String locationGPSSequence;
    @Column(name = "locationRef")
    private Integer locationRef;
    @Column(name = "stateRef")
    private Integer stateRef;

    public AgHarvestedIptcMetadata() {
    }

    public AgHarvestedIptcMetadata(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgHarvestedIptcMetadata(Integer idLocal, String locationDataOrigination, String locationGPSSequence) {
        this.idLocal = idLocal;
        this.locationDataOrigination = locationDataOrigination;
        this.locationGPSSequence = locationGPSSequence;
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

    public Integer getCityRef() {
        return cityRef;
    }

    public void setCityRef(Integer cityRef) {
        this.cityRef = cityRef;
    }

    public Integer getCopyrightState() {
        return copyrightState;
    }

    public void setCopyrightState(Integer copyrightState) {
        this.copyrightState = copyrightState;
    }

    public Integer getCountryRef() {
        return countryRef;
    }

    public void setCountryRef(Integer countryRef) {
        this.countryRef = countryRef;
    }

    public Integer getCreatorRef() {
        return creatorRef;
    }

    public void setCreatorRef(Integer creatorRef) {
        this.creatorRef = creatorRef;
    }

    public Integer getIsoCountryCodeRef() {
        return isoCountryCodeRef;
    }

    public void setIsoCountryCodeRef(Integer isoCountryCodeRef) {
        this.isoCountryCodeRef = isoCountryCodeRef;
    }

    public Integer getJobIdentifierRef() {
        return jobIdentifierRef;
    }

    public void setJobIdentifierRef(Integer jobIdentifierRef) {
        this.jobIdentifierRef = jobIdentifierRef;
    }

    public String getLocationDataOrigination() {
        return locationDataOrigination;
    }

    public void setLocationDataOrigination(String locationDataOrigination) {
        this.locationDataOrigination = locationDataOrigination;
    }

    public String getLocationGPSSequence() {
        return locationGPSSequence;
    }

    public void setLocationGPSSequence(String locationGPSSequence) {
        this.locationGPSSequence = locationGPSSequence;
    }

    public Integer getLocationRef() {
        return locationRef;
    }

    public void setLocationRef(Integer locationRef) {
        this.locationRef = locationRef;
    }

    public Integer getStateRef() {
        return stateRef;
    }

    public void setStateRef(Integer stateRef) {
        this.stateRef = stateRef;
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
        if (!(object instanceof AgHarvestedIptcMetadata)) {
            return false;
        }
        AgHarvestedIptcMetadata other = (AgHarvestedIptcMetadata) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgHarvestedIptcMetadata[ idLocal=" + idLocal + " ]";
    }
  }
