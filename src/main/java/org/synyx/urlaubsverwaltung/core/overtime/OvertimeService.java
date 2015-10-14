package org.synyx.urlaubsverwaltung.core.overtime;

import org.synyx.urlaubsverwaltung.core.person.Person;

import java.util.List;
import java.util.Optional;


/**
 * Provides possibility to create and fetch {@link Overtime} records.
 *
 * @author  Aljona Murygina - murygina@synyx.de
 * @since  2.11.0
 */
public interface OvertimeService {

    /**
     * Fetch all the overtime records for a certain person.
     *
     * @param  person  to fetch the overtime records for
     *
     * @return  list of overtime records the person has
     */
    List<Overtime> getOvertimeRecordsForPerson(Person person);


    /**
     * Saves an overtime record.
     *
     * @param  overtime  to be saved
     * @param  comment  contains further information to the overtime record, is optional
     * @param  author  identifies the person that recorded the overtime
     *
     * @return  the created overtime record
     */
    Overtime record(Overtime overtime, Optional<String> comment, Person author);
}