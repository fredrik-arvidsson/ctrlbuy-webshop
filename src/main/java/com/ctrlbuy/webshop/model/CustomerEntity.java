package com.ctrlbuy.webshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 🛡️ RAILWAY COMPATIBLE CustomerEntity
 *
 * Fullständig CustomerEntity med alla fields som CustomerService förväntar sig.
 * Inkluderar proper JPA annotations, validation, och timestamps.
 */
@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Användarnamn är obligatoriskt")
    @Size(min = 3, max = 50, message = "Användarnamn måste vara mellan 3 och 50 tecken")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "Lösenord är obligatoriskt")
    @Size(min = 6, message = "Lösenord måste vara minst 6 tecken")
    @Column(nullable = false)
    private String password;

    // ===== NYA FIELDS SOM CUSTOMERSERVICE BEHÖVER =====

    @NotBlank(message = "Namn är obligatoriskt")
    @Size(max = 100, message = "Namn får vara max 100 tecken")
    @Column(nullable = false, length = 100)
    private String name;

    @Email(message = "Ogiltig email-format")
    @Column(unique = true, length = 100)
    private String email;

    @Size(max = 20, message = "Telefonnummer får vara max 20 tecken")
    @Column(length = 20)
    private String phone;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== OPTIONAL EXTRA FIELDS =====

    @Size(max = 200, message = "Adress får vara max 200 tecken")
    @Column(length = 200)
    private String address;

    @Size(max = 50, message = "Stad får vara max 50 tecken")
    @Column(length = 50)
    private String city;

    @Size(max = 10, message = "Postnummer får vara max 10 tecken")
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Size(max = 50, message = "Land får vara max 50 tecken")
    @Column(length = 50)
    private String country;

    // ===== CONSTRUCTORS =====

    public CustomerEntity() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public CustomerEntity(String username, String password, String name) {
        this();
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public CustomerEntity(String username, String password, String name, String email) {
        this(username, password, name);
        this.email = email;
    }

    // ===== JPA LIFECYCLE CALLBACKS =====

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
        if (active == null) {
            active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== GETTERS AND SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ===== NYA GETTERS/SETTERS SOM CUSTOMERSERVICE BEHÖVER =====

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // Convenience method för boolean check
    public boolean isActive() {
        return active != null && active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ===== EXTRA GETTERS/SETTERS =====

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    // ===== UTILITY METHODS =====

    /**
     * 📧 getDisplayName - Visa namn eller username som fallback
     */
    public String getDisplayName() {
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }
        return username != null ? username : "Okänd kund";
    }

    /**
     * 📍 getFullAddress - Komplett adress som sträng
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();

        if (address != null && !address.trim().isEmpty()) {
            sb.append(address);
        }

        if (city != null && !city.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }

        if (postalCode != null && !postalCode.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(postalCode);
        }

        if (country != null && !country.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }

        return sb.toString();
    }

    /**
     * 📞 hasContactInfo - Kontrollera om kund har kontaktinfo
     */
    public boolean hasContactInfo() {
        return (email != null && !email.trim().isEmpty()) ||
                (phone != null && !phone.trim().isEmpty());
    }

    /**
     * 🆔 isNewCustomer - Kontrollera om detta är en ny kund
     */
    public boolean isNewCustomer() {
        return id == null;
    }

    // ===== OBJECT METHODS =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerEntity that = (CustomerEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "CustomerEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // ===== BUILDER PATTERN (OPTIONAL) =====

    /**
     * 🏗️ CustomerBuilder - Builder pattern för enkel skapande
     */
    public static class CustomerBuilder {
        private CustomerEntity customer;

        public CustomerBuilder() {
            customer = new CustomerEntity();
        }

        public CustomerBuilder username(String username) {
            customer.setUsername(username);
            return this;
        }

        public CustomerBuilder password(String password) {
            customer.setPassword(password);
            return this;
        }

        public CustomerBuilder name(String name) {
            customer.setName(name);
            return this;
        }

        public CustomerBuilder email(String email) {
            customer.setEmail(email);
            return this;
        }

        public CustomerBuilder phone(String phone) {
            customer.setPhone(phone);
            return this;
        }

        public CustomerBuilder address(String address) {
            customer.setAddress(address);
            return this;
        }

        public CustomerBuilder city(String city) {
            customer.setCity(city);
            return this;
        }

        public CustomerBuilder postalCode(String postalCode) {
            customer.setPostalCode(postalCode);
            return this;
        }

        public CustomerBuilder country(String country) {
            customer.setCountry(country);
            return this;
        }

        public CustomerBuilder active(boolean active) {
            customer.setActive(active);
            return this;
        }

        public CustomerEntity build() {
            return customer;
        }
    }

    /**
     * 🏗️ Static builder method
     */
    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }
}