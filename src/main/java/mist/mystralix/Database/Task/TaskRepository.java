package mist.mystralix.Database.Task;

import mist.mystralix.Database.BaseRepository;
import mist.mystralix.Objects.Task.Task;

/**
 * Repository abstraction for performing CRUD operations on Task data.
 *
 * <p>This interface defines the contract between the application layer
 * ({@code TaskService}) and the persistence layer (e.g., MySQL, SQLite, JSON files).
 * By programming against this interface rather than a concrete implementation,
 * the application follows the Dependency Inversion Principle (DIP), enabling
 * testability, modularity, and the ability to swap database technologies without
 * modifying service or command logic.</p>
 */
public interface TaskRepository extends BaseRepository<Task> { }
