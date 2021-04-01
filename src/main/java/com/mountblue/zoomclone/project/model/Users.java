package com.mountblue.zoomclone.project.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Users {

	@Id
	@SequenceGenerator(name = "user_sequence",
			sequenceName = "user_sequence",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,
			generator = "user_sequence")
	private int id;

	@NotNull
	private String name;

	@NotNull
	@Email(message = "Please provide a valid Email")
	private String email;

	@NotNull
   	@Length(min = 5, message = "Your password must have at least 5 characters")
	private String password;

	public Users(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}
}
