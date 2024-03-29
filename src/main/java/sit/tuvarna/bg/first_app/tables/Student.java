package sit.tuvarna.bg.first_app.tables;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Student")
    private Long id;

    @Column(name = "First_Name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "Last_Name", length = 50, nullable = false)
    private String lastName;

    @Column(name = "FN", length = 12, nullable = false )
    private String fn;

    @ManyToOne
    @JoinColumn(name = "Department_ID")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "Collage_ID")
    private College college;

    @ManyToOne
    @JoinColumn(name = "Room_ID")
    private Room room;
}
