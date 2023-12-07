package com.thinkboxberlin.stepserv.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "agents",
    indexes = { @Index(name = "lastUpdatedOn_index", columnList = "lastUpdatedOn", unique = false) })
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Agent {
    @Id
    private String agentUuid;
    private String agentName;
    private String currentLocation;
    @ElementCollection
    private List<String> tags = new ArrayList<String>();
    @UpdateTimestamp
    private Instant lastUpdatedOn;
}
